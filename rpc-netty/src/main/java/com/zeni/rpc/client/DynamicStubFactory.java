package com.zeni.rpc.client;

import com.itranswarp.compiler.JavaStringCompiler;
import com.zeni.rpc.transport.Transport;

import java.util.Map;

public class DynamicStubFactory implements StubFactory {

    private static final String STUB_SOURCE_TEMPLATE =
                    "package com.zeni.rpc.client.stubs;\n" +
                    "import com.zeni.rpc.serialize.SerializeSupport;\n" +
                    "\n" +
                    "public class %s extends AbstractStub implements %s {\n" +
                    "   @Override\n" +
                    "   public String %s(String arg) {\n" +
                    "       return SerializeSupport.parse(\n" +
                    "       invokeRemote(\n" +
                    "       new RpcRequest(\"%s\",\"%s\",SerializeSupport.serialize(arg))\n" +
                    "                )\n" +
                    "        );\n" +
                    "    }\n" +
                    "}";

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createStub(Transport transport, Class<T> serviceClass) {
        try {
            // 填充模板
            String stubSimpleName = serviceClass.getSimpleName() + "Stub";
            String classFullName = serviceClass.getName();
            String stubFullName = "com.zeni.rpc.client.stubs." + stubSimpleName;
            String methodName = serviceClass.getMethods()[0].getName();

            String source = String.format(STUB_SOURCE_TEMPLATE, stubSimpleName, classFullName, methodName, classFullName, methodName);

            //编译源码
            JavaStringCompiler compiler = new JavaStringCompiler();
            Map<String, byte[]> results = compiler.compile(stubSimpleName + ".java", source);

            // 加载编译好的类
            Class<?> clazz = compiler.loadClass(stubFullName, results);

            // 把 Transport 赋值给桩
            ServiceStub stubInstance = (ServiceStub) clazz.newInstance();
            stubInstance.setTransport(transport);

            //返回这个桩
            return (T) stubInstance;

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
