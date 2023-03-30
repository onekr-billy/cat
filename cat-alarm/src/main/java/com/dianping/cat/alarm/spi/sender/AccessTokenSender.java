package com.dianping.cat.alarm.spi.sender;

import com.dianping.cat.Cat;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * 基于访问令牌发送
 *
 * @author <a href="mailto:shiyindaxiaojie@gmail.com">gyl</a>
 * @since 2.4.13
 */
public abstract class AccessTokenSender extends AbstractSender {

	public static final String DEFAULT_COLOR = "#64BB6A";

	protected String httpPostSendByJson(String webHookURL, String body) {
		URL url;
		HttpURLConnection conn = null;
		InputStream in = null;
		try {
			url = new URL(webHookURL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(3000);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Charset", "UTF-8");
			//转换为字节数组
			byte[] data = body.getBytes(StandardCharsets.UTF_8);
			// 设置文件长度
			conn.setRequestProperty("Content-Length", String.valueOf(data.length));
			conn.setRequestProperty("Content-type", "application/json;charset=UTF-8");
			conn.connect();
			OutputStream out = conn.getOutputStream();
			// 写入请求的字符串
			out.write(data);
			out.flush();
			out.close();

			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				in = conn.getInputStream();
				return readBytes(in);
			}
			return null;
		} catch (Exception e) {
			m_logger.error("AccessToken send error: " + e.getMessage(), e);
			Cat.logError(webHookURL + ":" + body, e);
			return null;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception ignored) {
				}
			}
			close(conn);
		}
	}

	private static String readBytes(InputStream inputStream) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int readCount = 0;
		while ((readCount = inputStream.read(buffer)) > -1) {
			out.write(buffer, 0, readCount);
		}
		return out.toString("UTF-8");
	}

	private static void close(HttpURLConnection closeable) {
		if (closeable != null) {
			try {
				closeable.disconnect();
			} catch (Exception ignored) {
			}
		}
	}

	public static class AccessTokenResponseError extends Exception {
		public AccessTokenResponseError(String errMsg) {
			super(errMsg);
		}
	}
}
