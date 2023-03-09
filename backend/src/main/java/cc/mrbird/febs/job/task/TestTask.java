package cc.mrbird.febs.job.task;

import cc.mrbird.febs.cos.service.IStorehouseInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestTask {

    private final IStorehouseInfoService storehouseInfoService;

    public void test(String params) {
        log.info("我是带参数的test方法，正在被执行，参数为：{}" , params);
    }
    public void test1() {
        log.info("我是不带参数的test1方法，正在被执行");
    }

    /**
     * 任务盘库
     */
    public void replenishment() {
        storehouseInfoService.diskLibrary();
    }

}
