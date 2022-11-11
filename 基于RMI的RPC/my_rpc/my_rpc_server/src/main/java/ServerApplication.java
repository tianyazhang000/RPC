import com.rpc.RpcFactory;
import com.service.UserService;
import com.service.impl.UserServiceImpl;

public class ServerApplication {

    public static void main(String[] args) throws Exception {
//        //创建一个服务对象
//        UserService userService = new UserServiceImpl();
//        //注册
//        RpcFactory.registerService(UserService.class,userService);
        Class.forName("com.rpc.RpcFactory");
    }
}
