package com.general.etl;

import com.general.etl.assembler.AssemblerManager;
import com.general.etl.assembler.ICEAssember;
import com.general.etl.core.Context;
import com.general.etl.core.VendorEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@Log4j2
@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        ConfigurableApplicationContext appContext = new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE) // .REACTIVE, .SERVLET
                .run(args);

        AssemblerManager manager = appContext.getBean(AssemblerManager.class);
        manager.addAssembler(VendorEnum.TEST_VENDOR,appContext.getBean(ICEAssember.class));


        Context context = new Context(VendorEnum.TEST_VENDOR,Context.DEFAULT_BLOCK);
        manager.create(context);
        //multi thread in the certain depth
        try {
            manager.trigger();
        } catch (Exception e) {
            log.error("error occurred!!",e);
        }
        manager.destroy();




    }

}
