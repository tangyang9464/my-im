package com.common.zk;

import com.common.vo.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author: tangyang9464
 * @create: 2021-04-29 16:08
 **/
@Slf4j
public class CuratorUtil{
    private static String connectString="127.0.0.1:2181";
    private static CuratorFramework zkClient=CuratorFrameworkFactory.newClient(connectString,new RetryNTimes(Integer.MAX_VALUE, 1000));;
    public static String ZK_REGISTER_ROOT_PATH ="/my-im/server";

    public static void createNodeForServer(ServerInfo serverAdress){
        String path = ZK_REGISTER_ROOT_PATH + "/" + serverAdress.toString();
        try {
            if(zkClient.getState()!= CuratorFrameworkState.STARTED){
                zkClient.start();
            }
            if(zkClient.checkExists().forPath(path)!=null){
                log.info("节点已经存在");
            }
            else{
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAllNodeForServer() throws Exception {
        if(zkClient.getState()!= CuratorFrameworkState.STARTED){
            zkClient.start();
        }
        String path = CuratorUtil.ZK_REGISTER_ROOT_PATH;
        return zkClient.getChildren().forPath(path);
    }
    public static void deleteNodeForServer(ServerInfo serverAdress) {
        String path = ZK_REGISTER_ROOT_PATH + "/" +serverAdress.toString();
        if(zkClient.getState()!= CuratorFrameworkState.STARTED){
            zkClient.start();
        }
        try {
            zkClient.delete().forPath(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
