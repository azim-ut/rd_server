package app;

import java.util.concurrent.TimeUnit;

public class Timer {

    public static void printExecutionTime(long startTime, long endTime)
    {
        long time_ns = endTime - startTime;
        long time_ms = TimeUnit.NANOSECONDS.toMillis(time_ns);
        long time_sec = TimeUnit.NANOSECONDS.toSeconds(time_ns);
        long time_min = TimeUnit.NANOSECONDS.toMinutes(time_ns);
        long time_hour = TimeUnit.NANOSECONDS.toHours(time_ns);

        System.out.print("\nExecution Time: ");
        if(time_hour > 0)
            System.out.print(time_hour + " Hours, ");
        if(time_min > 0)
            System.out.print(time_min % 60 + " Minutes, ");
        if(time_sec > 0)
            System.out.print(time_sec % 60 + " Seconds, ");
        if(time_ms > 0)
            System.out.print(time_ms % 1E+3 + " MicroSeconds, ");
        if(time_ns > 0)
            System.out.print(time_ns % 1E+6 + " NanoSeconds");
    }

}
