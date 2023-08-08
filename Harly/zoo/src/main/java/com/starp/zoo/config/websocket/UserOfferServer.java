package com.starp.zoo.config.websocket;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author magic
 * @date 2021/1/22
 */
@Slf4j
@ServerEndpoint(value = "/userOffer/websocket")
@Component
public class UserOfferServer {

    @SuppressWarnings("MapOrSetKeyShouldOverrideHashCodeEquals")
    private static CopyOnWriteArraySet<UserOfferServer> webSocketSet = new CopyOnWriteArraySet<UserOfferServer>();
    private Session session;

    @Override
    public int hashCode(){
        return 1;
    }

    @SuppressFBWarnings("BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS")
    @Override
    public boolean equals(Object object){
        if(object!=null){
            UserOfferServer webSocketServer = (UserOfferServer) object;
            if(this.session.equals(webSocketServer.session)){
                return true;
            }else {
                return false;
            }
        }
        return false;
    }

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误"+error.toString());
        error.printStackTrace();
    }

    @SuppressFBWarnings("UC_USELESS_VOID_METHOD")
    @OnMessage
    public void onMessage(String message){
        for (UserOfferServer item : webSocketSet) {
            try {
                item.sendMessage("ping heart");
            } catch (Exception e) {
                continue;
            }
        }
    }

    public  void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendInfo(String message)  {
        for (UserOfferServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (Exception e) {
                continue;
            }
        }
    }
}
