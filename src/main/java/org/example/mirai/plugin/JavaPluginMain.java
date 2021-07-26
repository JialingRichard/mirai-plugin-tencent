package org.example.mirai.plugin;
import java.lang.*;
import java.io.*;

import net.mamoe.mirai.console.extension.PluginComponentStorage;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicLong;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.nlp.v20190408.NlpClient;
import com.tencentcloudapi.nlp.v20190408.models.*;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.ls.LSOutput;

/*
该插件由 嘉陵 开发
欢迎访问我的仓库
https://github.com/JialingRichard/mirai-plugin-tencent
如果帮助到您顺手点个star哦
        >.<
*/

public final class JavaPluginMain extends JavaPlugin {



    public static final JavaPluginMain INSTANCE = new JavaPluginMain();


    private JavaPluginMain() {
        super(new JvmPluginDescriptionBuilder("org.example.mirai-example", "0.1.0")
                .info("EG")
                .build());
    }


    String id = "腾讯api账号";
    String key = "腾讯api密码";
    long Qid = 12316; //QQ号


    int ADD1 = 0;
    int ADD = 0;
    int chatMode = 0;
    int chatTime = 0;
    int admin = 0;
    @Override
    public void onLoad(@NotNull PluginComponentStorage $this$onLoad) {
        File file = new File("config/miao/setting.txt");
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            id = reader.readLine();
            key = reader.readLine();
            Qid = Long.parseLong(reader.readLine());
            System.out.println("id: "+id);
            System.out.println("key: "+key);
            System.out.println("Qid: "+Qid);
            System.out.println("读取完毕！");
            reader.close();
        } catch (IOException e) {
            System.out.println("read error!\n");
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }


    @Override
    public void onEnable() {
        getLogger().info("日志");

        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, this::accept);
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, f -> {
            //监听好友消息
            getLogger().info(f.getMessage().contentToString());
        });
    }

    private void accept(GroupMessageEvent event) {
        //监听群消息
        if(chatTime>50){
            event.getSubject().sendMessage("我会想念大家的！喵呜");
            chatMode = 0;
        }
        ADD++;
        ADD1++;
        getLogger().info(event.getMessage().contentToString());
        String content = event.getMessage().contentToString();
        System.out.println("======\ncontent: "+content+"\n=========");
        if (event.getSender().getId() == Qid) {
            if (content.contains("禁止闲聊模式")) {
                chatMode = 3;
                event.getSubject().sendMessage("我好累啊喵！");
                chatTime = 0;
            }
            if (content.contains("开启闲聊模式")) {
                chatMode = 1;
                event.getSubject().sendMessage("快来和喵喵喵聊天吧！");
                chatTime = 0;
            }
            if (content.contains("关闭闲聊模式")) {
                chatMode = 0;
                event.getSubject().sendMessage("我会想念大家的！喵呜");
                chatTime = 0;
            }
            if(chatMode == 1){
                try {
                    // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
                    // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
                    Credential cred = new Credential(id, key);
                    // 实例化一个http选项，可选的，没有特殊需求可以跳过
                    HttpProfile httpProfile = new HttpProfile();
                    httpProfile.setEndpoint("nlp.tencentcloudapi.com");
                    // 实例化一个client选项，可选的，没有特殊需求可以跳过
                    ClientProfile clientProfile = new ClientProfile();
                    clientProfile.setHttpProfile(httpProfile);
                    // 实例化要请求产品的client对象,clientProfile是可选的
                    NlpClient client = new NlpClient(cred, "ap-guangzhou", clientProfile);
                    // 实例化一个请求对象,每个接口都会对应一个request对象
                    ChatBotRequest req = new ChatBotRequest();
                    req.setQuery(content);
                    // 返回的resp是一个ChatBotResponse的实例，与请求对象对应
                    ChatBotResponse resp = client.ChatBot(req);
                    // 输出json格式的字符串回包
                    System.out.println("=============================\n"
                            + ChatBotResponse.toJsonString(resp) + "" +
                            "\n============================\n");
                    char ss[] = new char[100];
                    ss = ChatBotResponse.toJsonString(resp).toCharArray();
                    //System.out.println("=========\n"+ss+"\n=======\n");
                    /*String str = ChatBotResponse.toJsonString(resp);*/
                    String str = new String(ss);
                    System.out.println("=========\n" + str + "\n=======\n");

                    char[] reply = new char[100];
                    /*for (int i = 0; i < 100; i++) {
                        reply[i] = 0;
                    }*/
                    int flag = 0;
                    int j = 0;
                    int i = 0;
                    for (i = 0; i < 100; i++) {
                        if ((flag == 3) && (ss[i] != '\"')) {
                            reply[j] = ss[i];
                            j++;
                        }
                        if (flag == 4) {
                            break;
                        }
                        if (ss[i] == '\"') {
                            flag++;
                        }
                    }
                    for (int q = 0; q < 100; q++) {
                        System.out.print(reply[i]);
                    }
                    String s = new String(reply).trim();
                   System.out.println("=========\n" + s + "\n=======\n");
                    if(content.contains("[图片]")){
                        // do nothing
                    }else{
                        event.getSubject().sendMessage(s);
                    }


                } catch (TencentCloudSDKException e) {
                    System.out.println(e.toString());
                    event.getSubject().sendMessage("error!");
                }
            } else if (content.contains("喵")) {
                try {
                    // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
                    // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
                    Credential cred = new Credential(id, key);
                    // 实例化一个http选项，可选的，没有特殊需求可以跳过
                    HttpProfile httpProfile = new HttpProfile();
                    httpProfile.setEndpoint("nlp.tencentcloudapi.com");
                    // 实例化一个client选项，可选的，没有特殊需求可以跳过
                    ClientProfile clientProfile = new ClientProfile();
                    clientProfile.setHttpProfile(httpProfile);
                    // 实例化要请求产品的client对象,clientProfile是可选的
                    NlpClient client = new NlpClient(cred, "ap-guangzhou", clientProfile);
                    // 实例化一个请求对象,每个接口都会对应一个request对象
                    ChatBotRequest req = new ChatBotRequest();
                    req.setQuery(content);
                    // 返回的resp是一个ChatBotResponse的实例，与请求对象对应
                    ChatBotResponse resp = client.ChatBot(req);
                    // 输出json格式的字符串回包
                    System.out.println("=============================\n"
                            + ChatBotResponse.toJsonString(resp) + "" +
                            "\n============================\n");
                    char ss[] = new char[100];
                    ss = ChatBotResponse.toJsonString(resp).toCharArray();
                    //System.out.println("=========\n"+ss+"\n=======\n");
                    /*String str = ChatBotResponse.toJsonString(resp);*/
                    String str = new String(ss);
                    System.out.println("=========\n" + str + "\n=======\n");

                    char[] reply = new char[100];
                    /*for (int i = 0; i < 100; i++) {
                        reply[i] = 0;
                    }*/
                    int flag = 0;
                    int j = 0;
                    int i = 0;
                    for (i = 0; i < 100; i++) {
                        if ((flag == 3) && (ss[i] != '\"')) {
                            reply[j] = ss[i];
                            j++;
                        }
                        if (flag == 4) {
                            break;
                        }
                        if (ss[i] == '\"') {
                            flag++;
                        }
                    }
                    for (int q = 0; q < 100; q++) {
                        System.out.print(reply[i]);
                    }
                    String s = new String(reply).trim();
                    System.out.println("=========\n" + s + "\n=======\n");
                    if(content.contains("[图片]")){
                        // do nothing
                    }else{
                        event.getSubject().sendMessage(s);
                    }
                } catch (TencentCloudSDKException e) {
                    System.out.println(e.toString());
                    event.getSubject().sendMessage("error!");
                }
            }else if(chatMode == 2){
                try {
                    // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
                    // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
                    Credential cred = new Credential(id, key);
                    // 实例化一个http选项，可选的，没有特殊需求可以跳过
                    HttpProfile httpProfile = new HttpProfile();
                    httpProfile.setEndpoint("nlp.tencentcloudapi.com");
                    // 实例化一个client选项，可选的，没有特殊需求可以跳过
                    ClientProfile clientProfile = new ClientProfile();
                    clientProfile.setHttpProfile(httpProfile);
                    // 实例化要请求产品的client对象,clientProfile是可选的
                    NlpClient client = new NlpClient(cred, "ap-guangzhou", clientProfile);
                    // 实例化一个请求对象,每个接口都会对应一个request对象
                    ChatBotRequest req = new ChatBotRequest();
                    req.setQuery(content);
                    // 返回的resp是一个ChatBotResponse的实例，与请求对象对应
                    ChatBotResponse resp = client.ChatBot(req);
                    // 输出json格式的字符串回包
                    System.out.println("=============================\n"
                            + ChatBotResponse.toJsonString(resp) + "" +
                            "\n============================\n");
                    char ss[] = new char[100];
                    ss = ChatBotResponse.toJsonString(resp).toCharArray();
                    //System.out.println("=========\n"+ss+"\n=======\n");
                    /*String str = ChatBotResponse.toJsonString(resp);*/
                    String str = new String(ss);
                    System.out.println("=========\n" + str + "\n=======\n");

                    char[] reply = new char[100];
                    /*for (int i = 0; i < 100; i++) {
                        reply[i] = 0;
                    }*/
                    int flag = 0;
                    int j = 0;
                    int i = 0;
                    for (i = 0; i < 100; i++) {
                        if ((flag == 3) && (ss[i] != '\"')) {
                            reply[j] = ss[i];
                            j++;
                        }
                        if (flag == 4) {
                            break;
                        }
                        if (ss[i] == '\"') {
                            flag++;
                        }
                    }
                    for (int q = 0; q < 100; q++) {
                        System.out.print(reply[i]);
                    }
                    String s = new String(reply).trim();
                    System.out.println("=========\n" + s + "\n=======\n");
                    if(content.contains("[图片]")){
                        // do nothing
                    }else{
                        event.getSubject().sendMessage(s);
                    }
                } catch (TencentCloudSDKException e) {
                    System.out.println(e.toString());
                    event.getSubject().sendMessage("error!");
                }
                chatTime += 5;
            }

        } else {
//            if (ADD == 2000) {
//                long num = event.getSender().getId();
//                event.getSubject().sendMessage(event.getSenderName());
//                event.getSubject().sendMessage("你说得对!!!");
//
//                // content.contains("[mirai:at:123456]")
//                // event.getSubject().sendMessage(event.getMessage());
//            }
            if (content.contains("开启闲聊模式")) {
                if(chatMode == 3){
                    chatMode = 3;
                    event.getSubject().sendMessage("主人喜欢安静哦");
                    chatTime = 0;
                }else {
                    chatMode = 2;
                    event.getSubject().sendMessage("快来和喵喵喵聊天吧！");
                    chatTime = 0;
                }
            }
            if(chatMode == 1){
                try {
                    // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
                    // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
                    Credential cred = new Credential(id, key);
                    // 实例化一个http选项，可选的，没有特殊需求可以跳过
                    HttpProfile httpProfile = new HttpProfile();
                    httpProfile.setEndpoint("nlp.tencentcloudapi.com");
                    // 实例化一个client选项，可选的，没有特殊需求可以跳过
                    ClientProfile clientProfile = new ClientProfile();
                    clientProfile.setHttpProfile(httpProfile);
                    // 实例化要请求产品的client对象,clientProfile是可选的
                    NlpClient client = new NlpClient(cred, "ap-guangzhou", clientProfile);
                    // 实例化一个请求对象,每个接口都会对应一个request对象
                    ChatBotRequest req = new ChatBotRequest();
                    req.setQuery(content);
                    // 返回的resp是一个ChatBotResponse的实例，与请求对象对应
                    ChatBotResponse resp = client.ChatBot(req);
                    // 输出json格式的字符串回包
                    System.out.println("=============================\n"
                            + ChatBotResponse.toJsonString(resp) + "" +
                            "\n============================\n");
                    char ss[] = new char[100];
                    ss = ChatBotResponse.toJsonString(resp).toCharArray();
                    //System.out.println("=========\n"+ss+"\n=======\n");
                    /*String str = ChatBotResponse.toJsonString(resp);*/
                    String str = new String(ss);
                    System.out.println("=========\n" + str + "\n=======\n");

                    char[] reply = new char[100];
                    /*for (int i = 0; i < 100; i++) {
                        reply[i] = 0;
                    }*/
                    int flag = 0;
                    int j = 0;
                    int i = 0;
                    for (i = 0; i < 100; i++) {
                        if ((flag == 3) && (ss[i] != '\"')) {
                            reply[j] = ss[i];
                            j++;
                        }
                        if (flag == 4) {
                            break;
                        }
                        if (ss[i] == '\"') {
                            flag++;
                        }
                    }
                    for (int q = 0; q < 100; q++) {
                        System.out.print(reply[i]);
                    }
                    String s = new String(reply).trim();
                   System.out.println("=========\n" + s + "\n=======\n");
                    if(content.contains("[图片]")){
                        // do nothing
                    }else{
                        event.getSubject().sendMessage(s);
                    }
                } catch (TencentCloudSDKException e) {
                    System.out.println(e.toString());
                    event.getSubject().sendMessage("error!");
                }
                chatTime++;
            } else if (ADD1 == 500) {
                try {
                    // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
                    // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
                    Credential cred = new Credential(id, key);
                    // 实例化一个http选项，可选的，没有特殊需求可以跳过
                    HttpProfile httpProfile = new HttpProfile();
                    httpProfile.setEndpoint("nlp.tencentcloudapi.com");
                    // 实例化一个client选项，可选的，没有特殊需求可以跳过
                    ClientProfile clientProfile = new ClientProfile();
                    clientProfile.setHttpProfile(httpProfile);
                    // 实例化要请求产品的client对象,clientProfile是可选的
                    NlpClient client = new NlpClient(cred, "ap-guangzhou", clientProfile);
                    // 实例化一个请求对象,每个接口都会对应一个request对象
                    ChatBotRequest req = new ChatBotRequest();
                    req.setQuery(content);
                    // 返回的resp是一个ChatBotResponse的实例，与请求对象对应
                    ChatBotResponse resp = client.ChatBot(req);
                    // 输出json格式的字符串回包
                    System.out.println("=============================\n"
                            + ChatBotResponse.toJsonString(resp) + "" +
                            "\n============================\n");
                    char ss[] = new char[100];
                    ss = ChatBotResponse.toJsonString(resp).toCharArray();
                    //System.out.println("=========\n"+ss+"\n=======\n");
                    /*String str = ChatBotResponse.toJsonString(resp);*/
                    String str = new String(ss);
                    System.out.println("=========\n" + str + "\n=======\n");

                    char[] reply = new char[100];
                    /*for (int i = 0; i < 100; i++) {
                        reply[i] = 0;
                    }*/
                    int flag = 0;
                    int j = 0;
                    int i = 0;
                    for (i = 0; i < 100; i++) {
                        if ((flag == 3) && (ss[i] != '\"')) {
                            reply[j] = ss[i];
                            j++;
                        }
                        if (flag == 4) {
                            break;
                        }
                        if (ss[i] == '\"') {
                            flag++;
                        }
                    }
                    for (int q = 0; q < 100; q++) {
                        System.out.print(reply[i]);
                    }
                    String s = new String(reply).trim();
                   System.out.println("=========\n" + s + "\n=======\n");
                    if(content.contains("[图片]")){
                        // do nothing
                    }else{
                        event.getSubject().sendMessage(s);
                    }
                } catch (TencentCloudSDKException e) {
                    System.out.println(e.toString());
                    event.getSubject().sendMessage("error!");
                }
            }else if (content.contains("喵")) {
                try {
                    // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
                    // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
                    Credential cred = new Credential(id, key);
                    // 实例化一个http选项，可选的，没有特殊需求可以跳过
                    HttpProfile httpProfile = new HttpProfile();
                    httpProfile.setEndpoint("nlp.tencentcloudapi.com");
                    // 实例化一个client选项，可选的，没有特殊需求可以跳过
                    ClientProfile clientProfile = new ClientProfile();
                    clientProfile.setHttpProfile(httpProfile);
                    // 实例化要请求产品的client对象,clientProfile是可选的
                    NlpClient client = new NlpClient(cred, "ap-guangzhou", clientProfile);
                    // 实例化一个请求对象,每个接口都会对应一个request对象
                    ChatBotRequest req = new ChatBotRequest();
                    req.setQuery(content);
                    // 返回的resp是一个ChatBotResponse的实例，与请求对象对应
                    ChatBotResponse resp = client.ChatBot(req);
                    // 输出json格式的字符串回包
                    System.out.println("=============================\n"
                            + ChatBotResponse.toJsonString(resp) + "" +
                            "\n============================\n");
                    char ss[] = new char[100];
                    ss = ChatBotResponse.toJsonString(resp).toCharArray();
                    //System.out.println("=========\n"+ss+"\n=======\n");
                    /*String str = ChatBotResponse.toJsonString(resp);*/
                    String str = new String(ss);
                    System.out.println("=========\n" + str + "\n=======\n");

                    char[] reply = new char[100];
                    /*for (int i = 0; i < 100; i++) {
                        reply[i] = 0;
                    }*/
                    int flag = 0;
                    int j = 0;
                    int i = 0;
                    for (i = 0; i < 100; i++) {
                        if ((flag == 3) && (ss[i] != '\"')) {
                            reply[j] = ss[i];
                            j++;
                        }
                        if (flag == 4) {
                            break;
                        }
                        if (ss[i] == '\"') {
                            flag++;
                        }
                    }
                    for (int q = 0; q < 100; q++) {
                        System.out.print(reply[i]);
                    }
                    String s = new String(reply).trim();
                   System.out.println("=========\n" + s + "\n=======\n");
                    if(content.contains("[图片]")){
                        // do nothing
                    }else{
                        event.getSubject().sendMessage(s);
                    }
                } catch (TencentCloudSDKException e) {
                    System.out.println(e.toString());
                    event.getSubject().sendMessage("error!");
                }
            }else if(chatMode == 2){
                try {
                    // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
                    // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
                    Credential cred = new Credential(id, key);
                    // 实例化一个http选项，可选的，没有特殊需求可以跳过
                    HttpProfile httpProfile = new HttpProfile();
                    httpProfile.setEndpoint("nlp.tencentcloudapi.com");
                    // 实例化一个client选项，可选的，没有特殊需求可以跳过
                    ClientProfile clientProfile = new ClientProfile();
                    clientProfile.setHttpProfile(httpProfile);
                    // 实例化要请求产品的client对象,clientProfile是可选的
                    NlpClient client = new NlpClient(cred, "ap-guangzhou", clientProfile);
                    // 实例化一个请求对象,每个接口都会对应一个request对象
                    ChatBotRequest req = new ChatBotRequest();
                    req.setQuery(content);
                    // 返回的resp是一个ChatBotResponse的实例，与请求对象对应
                    ChatBotResponse resp = client.ChatBot(req);
                    // 输出json格式的字符串回包
                    System.out.println("=============================\n"
                            + ChatBotResponse.toJsonString(resp) + "" +
                            "\n============================\n");
                    char ss[] = new char[100];
                    ss = ChatBotResponse.toJsonString(resp).toCharArray();
                    //System.out.println("=========\n"+ss+"\n=======\n");
                    /*String str = ChatBotResponse.toJsonString(resp);*/
                    String str = new String(ss);
                    System.out.println("=========\n" + str + "\n=======\n");

                    char[] reply = new char[100];
                    /*for (int i = 0; i < 100; i++) {
                        reply[i] = 0;
                    }*/
                    int flag = 0;
                    int j = 0;
                    int i = 0;
                    for (i = 0; i < 100; i++) {
                        if ((flag == 3) && (ss[i] != '\"')) {
                            reply[j] = ss[i];
                            j++;
                        }
                        if (flag == 4) {
                            break;
                        }
                        if (ss[i] == '\"') {
                            flag++;
                        }
                    }
                    for (int q = 0; q < 100; q++) {
                        System.out.print(reply[i]);
                    }
                    String s = new String(reply).trim();
                   System.out.println("=========\n" + s + "\n=======\n");
                    if(content.contains("[图片]")){
                        // do nothing
                    }else{
                        event.getSubject().sendMessage(s);
                    }
                } catch (TencentCloudSDKException e) {
                    System.out.println(e.toString());
                    event.getSubject().sendMessage("error!");
                }
                chatTime += 5;
            }

        }


    }
}



/*
   在settings.gradle.kts里改生成的插件.jar名称
本插件项目是在https://github.com/Nambers/mirai_plugin_example中的模板基础上开发的
    感谢这位同志的模板
使用了腾讯云Java SDK
    https://github.com/TencentCloud/tencentcloud-sdk-java
*/
