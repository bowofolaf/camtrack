package blf.com.camtrack.client;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
/**
 * Created by bfadojutimi on 3/16/2015.
 */
public class RestClient extends AsyncTask<String, Void, String> {

    DownloadListener downloadListener;
    String body;
    String host;

    public RestClient(String body,String host)
    {
        this.body = body;
        this.host = host;
    }

    protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException,IOException
    {
        InputStream in = entity.getContent();
        StringBuffer out = new StringBuffer();
        int n = 1;
        while(n>0){
            byte[] b = new byte[4096];
            n = in.read(b);
            if(n>0) out.append(new String(b,0,n));
        }
        return out.toString();
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(host);

        StringEntity request;
        try {
            request = new StringEntity(body);
        } catch (UnsupportedEncodingException e1) {
            return e1.getLocalizedMessage();
        }
        request.setContentType("application/xml");

        httpPost.setEntity(request);

        String text = null;

        try {
            HttpResponse response = httpClient.execute(httpPost, localContext);
            HttpEntity entity = response.getEntity();
            text = getASCIIContentFromEntity(entity);
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
        return text;
    }

    @Override
    protected void onPostExecute(String result) {
        if(result!=null)
        {
            downloadListener.onSuccesfulDownload(result);
        }
        else
        {
            downloadListener.onFailedDownload(result);
        }
    }

    public void SetListener(DownloadListener listener)
    {
        this.downloadListener = listener;
    }

    public interface DownloadListener{
        void onSuccesfulDownload(String result);
        void onFailedDownload(String result);
    }

}