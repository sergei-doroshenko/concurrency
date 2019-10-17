package org.sdoroshenko.taskcancellation;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Hystrix command wraps Http client.
 * Before running - start {@link org.sdoroshenko.http.DelayedHttpServer#main(String[])}.
 */
public class HystrixHttpCommand extends HystrixCommand<String> {

    private static final Logger logger = LoggerFactory.getLogger(HystrixHttpCommand.class);
    private static final Executor executor = Executor.newInstance(HttpClients.custom().disableContentCompression().build());

    protected HystrixHttpCommand() {
        super(setter());
    }

    @Override
    protected String run() throws Exception {
        Request httpRequest = Request.Get("http://localhost:8080/myapp/myresource")
            /*
             Go to java.net.Socket.setSoTimeout(int timeout).
             With this option set to a non-zero timeout, a read() call on the InputStream associated with
             this Socket will block for only this amount of time.
             If the timeout expires, a java.net.SocketTimeoutException is raised
             */
            .socketTimeout(100)
            /*
              Go to java.net.Socket.connect(SocketAddress endpoint, int timeout) as a timeout.
              Connects this socket to the server with a specified timeout value.
              Throws java.net.SocketTimeoutException if timeout expires before connecting.
             */
            .connectTimeout(300);
        Response response = executor.execute(httpRequest);
        return response.handleResponse(new BasicResponseHandler());
    }

    @Override
    protected String getFallback() {
        Throwable t = getExecutionException();
        logger.error("Fallback on " + t);
        return "Fallback";
    }

    private static Setter setter() {
        return Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("task-cancellation"))
            .andCommandKey(HystrixCommandKey.Factory.asKey("http-command"))
            .andCommandPropertiesDefaults(
                HystrixCommandProperties.Setter()
                    .withExecutionTimeoutInMilliseconds(2_000)
            );
    }

    public static void main(String[] args) throws InterruptedException {

        try {
            Future<String> future = new HystrixHttpCommand().queue();
            logger.debug(future.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            logger.error("HttpCommand exception: " + e.getCause().getMessage());
        }

        TimeUnit.SECONDS.sleep(1);
        System.exit(0);
    }
}
