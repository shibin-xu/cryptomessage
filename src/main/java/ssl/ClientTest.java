package ssl;


public class ClientTest {

    public static void main(String[] args){
        //Test Registration
        ServerResponse r1 = ClientUtil.register("r1", "passwordr1");
        System.out.println("("+r1.isGood()+")"+r1.getData()+"      which should be (true)");
        ServerResponse r2 = ClientUtil.register("r1", "passwordr2");
        System.out.println("("+r2.isGood()+")"+r2.getData()+"      which should be (false)");

        //Test Login
        ServerResponse r3 = ClientUtil.login("r1", "passwordr1");
        System.out.println("("+r3.isGood()+")"+r3.getData()+"      which should be (true)");
        ServerResponse r4 = ClientUtil.login("r1", "passwordr2");
        System.out.println("("+r4.isGood()+")"+r4.getData()+"      which should be (false)");
        ServerResponse r5 = ClientUtil.login("r2", "passwordr2");
        System.out.println("("+r5.isGood()+")"+r5.getData()+"      which should be (false)");

        //Test Decrypt
        ServerResponse r6 = ClientUtil.decryptSymmetric("Matt", "12345", "aMFwuv15l+YG2GH9JQLPwBorVSJEDDVGhBCitcFHsrtzEKHbmfsI/tl/awtERwxrNWTY60NnxqF5VhXlbLMw4zW+qZ2EpKYNKkky4peFNJPKFZmuWHL7zKHdHYfQqjEmmNXfhvoA+DL/qVXZXeMAj+LnUyjFqidjPdudYCRHhxn6EqKWHYcZ11iKp4RUvq0WV8wZJc7XUALLoV2R/TmJ3hgB8QLpsMzsYIa6+00b6NA0LRIA9ux9q5MRMyvsFXY6qfzg76rk5gNuG4S2dI/a781yTjs1+350BPsLMwjuTBb0yWCNP8jpW5m0fQ7tbe0V/GlCL+PdUGHudLHj9biWiw==");
        System.out.println("("+r6.isGood()+")"+r6.getData()+"      which should be (true)");
        
	//Test queryuser
        ServerResponse r7 = ClientUtil.queryuser("r1");
        System.out.println("("+r7.isGood()+")"+r7.getData()+"      which should be (true)");
        ServerResponse r8 = ClientUtil.queryuser("r1");
        System.out.println("("+r4.isGood()+")"+r8.getData()+"      which should be (false)");
        ServerResponse r9 = ClientUtil.queryuser("r2");
        System.out.println("("+r9.isGood()+")"+r9.getData()+"      which should be (false)");


    }


}
