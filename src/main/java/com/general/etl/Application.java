package com.general.etl;

import com.general.etl.assembler.AssemblerManager;
import com.general.etl.assembler.TradeAssembler;
import com.general.etl.core.Context;
import com.general.etl.core.VendorEnum;
import com.general.etl.exception.CreationException;
import com.general.etl.exception.DestroyException;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        ConfigurableApplicationContext appContext = new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE) // .REACTIVE, .SERVLET
                .run(args);
        AssemblerManager manager = appContext.getBean(AssemblerManager.class);

        manager.addAssembler(VendorEnum.TEST_VENDOR,appContext.getBean(TradeAssembler.class));
        try {
            manager.create();
        } catch (CreationException e) {
            e.printStackTrace();
        }
        Context context = new Context(VendorEnum.TEST_VENDOR);
        manager.trigger(context);
        try {
            manager.destroy();
        } catch (DestroyException e) {
            e.printStackTrace();
        }




    }

}
