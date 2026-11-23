package org.example.cuican.Controller.admin;

import org.example.cuican.Bean.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 服务器信息监控控制器
 * 获取服务器系统信息、CPU使用率、内存使用率、磁盘使用率等监控数据
 * 供管理员后台查看服务器运行状态
 */
@RestController
@RequestMapping("/admin/server")
public class AdminServerController {
    /**
     * 获取服务器基础信息接口
     * 接口路径：/admin/server/info
     * 请求方式：GET
     * @return 统一响应结果，返回包含操作系统信息、Java版本、启动时间、运行时长、CPU核心数等基础信息
     */
    @GetMapping("/info")
    public Result info() {
        Properties props = System.getProperties();
        Runtime runtime = Runtime.getRuntime();
        RuntimeMXBean mxBean = ManagementFactory.getRuntimeMXBean();

        Map<String, Object> info = new HashMap<>();
        info.put("osName", props.getProperty("os.name"));
        info.put("osArch", props.getProperty("os.arch"));
        info.put("javaVersion", props.getProperty("java.version"));
        info.put("javaHome", props.getProperty("java.home"));
        info.put("userName", props.getProperty("user.name"));
        info.put("userDir", props.getProperty("user.dir"));
        info.put("startTime", new Date(mxBean.getStartTime()));
        info.put("uptime", formatUptime(mxBean.getUptime()));
        info.put("processors", runtime.availableProcessors());
        return Result.success(info);
    }

    /**
     * 获取服务器实时监控数据接口
     * 接口路径：/admin/server/monitor
     * 请求方式：GET
     * @return 统一响应结果，返回包含CPU使用率、JVM内存使用、系统内存使用、磁盘使用率等实时监控数据
     */
    @GetMapping("/monitor")
    public Result monitor() {
        Runtime runtime = Runtime.getRuntime();
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memBean = ManagementFactory.getMemoryMXBean();
        DecimalFormat df = new DecimalFormat("#.0");

        Map<String, Object> data = new HashMap<>();
        // CPU - try getProcessCpuLoad first (more accurate on Windows)
        double cpuLoad = osBean.getSystemLoadAverage();
        double cpuUsage = -1;
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunBean = (com.sun.management.OperatingSystemMXBean) osBean;
            double c1 = sunBean.getProcessCpuLoad();
            if (c1 >= 0) {
                cpuUsage = c1;
            } else {
                double c2 = sunBean.getCpuLoad();
                if (c2 >= 0) cpuUsage = c2;
            }
        }
        if (cpuUsage < 0 && cpuLoad >= 0) {
            cpuUsage = cpuLoad / runtime.availableProcessors();
        }
        data.put("cpuLoad", cpuLoad < 0 ? "N/A" : df.format(cpuLoad));
        data.put("cpuUsage", cpuUsage < 0 ? "N/A" : df.format(cpuUsage * 100) + "%");
        data.put("cpuCores", runtime.availableProcessors());

        // 内存 (JVM)
        MemoryUsage heap = memBean.getHeapMemoryUsage();
        long usedMem = heap.getUsed();
        long maxMem = heap.getMax();
        data.put("jvmUsed", formatBytes(usedMem));
        data.put("jvmMax", formatBytes(maxMem));
        data.put("jvmRate", df.format((double) usedMem / maxMem * 100) + "%");
        data.put("jvmTotal", formatBytes(runtime.totalMemory()));
        data.put("jvmFree", formatBytes(runtime.freeMemory()));

        // 系统内存
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunBean = (com.sun.management.OperatingSystemMXBean) osBean;
            long totalPhys = sunBean.getTotalMemorySize();
            long freePhys = sunBean.getFreeMemorySize();
            data.put("sysTotal", formatBytes(totalPhys));
            data.put("sysUsed", formatBytes(totalPhys - freePhys));
            data.put("sysRate", df.format((double)(totalPhys - freePhys) / totalPhys * 100) + "%");
        }

        // 磁盘
        File[] roots = File.listRoots();
        long diskTotal = 0, diskFree = 0;
        for (File root : roots) {
            diskTotal += root.getTotalSpace();
            diskFree += root.getFreeSpace();
        }
        data.put("diskTotal", formatBytes(diskTotal));
        data.put("diskFree", formatBytes(diskFree));
        data.put("diskUsed", formatBytes(diskTotal - diskFree));
        data.put("diskRate", df.format((double)(diskTotal - diskFree) / diskTotal * 100) + "%");

        return Result.success(data);
    }

    /**
     * 格式化字节数为人类可读格式（B/KB/MB/GB/TB）
     * @param bytes 字节数
     * @return 格式化后的字符串
     */
    private String formatBytes(long bytes) {
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int idx = 0;
        double v = bytes;
        while (v >= 1024 && idx < units.length - 1) { v /= 1024; idx++; }
        return new DecimalFormat("#.0").format(v) + " " + units[idx];
    }

    /**
     * 格式化运行时长为天小时分钟格式
     * @param ms 毫秒数
     * @return 格式化后的字符串，如 "2天3小时15分钟"
     */
    private String formatUptime(long ms) {
        long days = ms / 86400000; ms %= 86400000;
        long hours = ms / 3600000; ms %= 3600000;
        long mins = ms / 60000;
        return days + "天" + hours + "小时" + mins + "分钟";
    }
}
