package cn.humorchen.methodcache.lru;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * lru算法实现key管理
 * least recently use 最近未使用淘汰，使用了就放最前面
 * @author humorchen
 * @date 2022/7/26 20:40
 */
public class MethodCacheKeyLruManager implements MethodCacheKeyManager{
    /**
     * key最大数量
     */
    private int maxKeySize;
    /**
     * 节点map
     */
    private Map<String,Node> nodeMap = new ConcurrentHashMap<>();
    /**
     * 头结点
     */
    private Node first;
    /**
     * 尾结点
     */
    private Node last;

    public MethodCacheKeyLruManager(int maxKeySize) {
        this.maxKeySize = maxKeySize;
    }

    /**
     * 使用记录
     *
     * @param key
     */
    @Override
    public void record(String key) {

    }

    /**
     * 写入一个key
     * 无需淘汰其他key则返回null
     * 需要淘汰某个key则返回对应key
     *
     * @param key
     * @return
     */
    @Override
    public String write(String key) {
        Node node = nodeMap.get(key);
        String pop = null;
        if (node == null){
            // 不存在
            if (nodeMap.size() >= maxKeySize){
                // 内存不够了
                pop = popNode();
            }
            pushNode(new Node(key));
        }else {
            // 已存在移动到最前面
            if (node != first){
                moveToFirst(node);
            }
        }
        return pop;
    }

    /**
     * 弹出一个节点
     * @return
     */
    private String popNode(){
        if (last == null){
            return null;
        }
        String removed = last.key;

        Node last = this.last;
        this.last = last.pre;
        this.last.next = last.pre = null;

        nodeMap.remove(removed);

        return removed;
    }

    /**
     * 写入节点
     * @param node
     */
    private void pushNode(Node node){
        if (first == null){
            first = node;
        }else if (last == null){
            last = first;
            first = node;
            first.next = last;
            last.pre = first;
        }else {
            Node first = this.first;
            first.pre = this.first = node;
            this.first.next = first;
        }
        nodeMap.put(node.key,node);
    }

    /**
     * 把该节点移动到最前面
     * @param node
     */
    private void moveToFirst(Node node){
        // 删除自己
        if (node == last){
            popNode();
        }else {
            // 先从自己前后节点剔除自己
            Node myPreNode = node.pre;
            Node myNextNode = node.next;
            if (myPreNode != null){
                myPreNode.next = myNextNode;
            }
            if (myNextNode != null){
                myNextNode.pre = myPreNode;
            }
        }
        // 推入最前端
        pushNode(node);
    }

    /**
     * LRU node
     */
    class Node{
        /**
         * 自己的key
         */
        private String key;
        /**
         * 上一个节点
         */
        private Node pre;
        /**
         * 下一个节点
         */
        private Node next;

        public Node(String key) {
            this.key = key;
        }
    }

    public static void main(String[] args) {
        MethodCacheKeyLruManager keyLruManager = new MethodCacheKeyLruManager(4);
        Random random = new Random();
        for (int i = 0;i<16;i++){
            String key = "testkey-"+random.nextInt(10);
            String write = keyLruManager.write(key);
            System.out.println("写入了key："+key);
            if (write != null){
                System.out.println("移除了key："+write);
            }
        }
    }
}
