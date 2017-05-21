package com.lianlianpay.demo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lianlianpay.utils.FuncUtils;
import com.lianlianpay.utils.TraderRSAUtil;

public class HttpURLConnectionPost {

	// 开发环境
	private static String TRADER_PRI_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOilN4tR7HpNYvSBra/DzebemoAiGtGeaxa+qebx/O2YAdUFPI+xTKTX2ETyqSzGfbxXpmSax7tXOdoa3uyaFnhKRGRvLdq1kTSTu7q5s6gTryxVH2m62Py8Pw0sKcuuV0CxtxkrxUzGQN+QSxf+TyNAv5rYi/ayvsDgWdB3cRqbAgMBAAECgYEAj02d/jqTcO6UQspSY484GLsL7luTq4Vqr5L4cyKiSvQ0RLQ6DsUG0g+Gz0muPb9ymf5fp17UIyjioN+ma5WquncHGm6ElIuRv2jYbGOnl9q2cMyNsAZCiSWfR++op+6UZbzpoNDiYzeKbNUz6L1fJjzCt52w/RbkDncJd2mVDRkCQQD/Uz3QnrWfCeWmBbsAZVoM57n01k7hyLWmDMYoKh8vnzKjrWScDkaQ6qGTbPVL3x0EBoxgb/smnT6/A5XyB9bvAkEA6UKhP1KLi/ImaLFUgLvEvmbUrpzY2I1+jgdsoj9Bm4a8K+KROsnNAIvRsKNgJPWd64uuQntUFPKkcyfBV1MXFQJBAJGs3Mf6xYVIEE75VgiTyx0x2VdoLvmDmqBzCVxBLCnvmuToOU8QlhJ4zFdhA1OWqOdzFQSw34rYjMRPN24wKuECQEqpYhVzpWkA9BxUjli6QUo0feT6HUqLV7O8WqBAIQ7X/IkLdzLa/vwqxM6GLLMHzylixz9OXGZsGAkn83GxDdUCQA9+pQOitY0WranUHeZFKWAHZszSjtbe6wDAdiKdXCfig0/rOdxAODCbQrQs7PYy1ed8DuVQlHPwRGtokVGHATU=";
	private final static String SERVER = "http://localhost:8080//merchant/trustee/trusteeFileDownload.htm";
	private static File destFile=new File("D:\\test.txt");
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		readContentFromPost();
	}

	public static void readContentFromPost() throws IOException {
		try {
 
			JSONObject reqJson = new JSONObject();
			reqJson.put("oid_partner", "201310102000003524");
			reqJson.put("sign_type", "RSA");
			reqJson.put("sign", genRSASign(JSON.parseObject(JSON.toJSONString(reqJson))));
			String reqStr = JSON.toJSONString(reqJson);
			System.out.println("请求报文为:" + reqStr);

			// 创建url资源
			URL url = new URL(SERVER);
			// 建立http连接
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 设置允许输出
			conn.setDoOutput(true);

			conn.setDoInput(true);

			// 设置不用缓存
			conn.setUseCaches(false);
			// 设置传递方式
			conn.setRequestMethod("POST");
			// 设置维持长连接
			conn.setRequestProperty("Connection", "Keep-Alive");
			// 设置文件字符集:
			conn.setRequestProperty("Charset", "UTF-8");
			// 转换为字节数组
			byte[] data = (reqStr).getBytes();
			// 设置文件长度
			conn.setRequestProperty("Content-Length", String.valueOf(data.length));
			// 设置文件类型:
			conn.setRequestProperty("contentType", "application/json");
			// 开始连接请求
			conn.connect();
			OutputStream out = conn.getOutputStream();
			// 写入请求的字符串
			out.write((reqStr).getBytes());
			out.flush();
			out.close();

			System.out.println(conn.getResponseCode());
			// 请求返回的状态
			if (conn.getResponseCode() == 200) {
				System.out.println("连接成功");
				// 请求返回的数据
				InputStream is = conn.getInputStream();
				RandomAccessFile raf = null;
				raf = new RandomAccessFile(destFile, "rw");
				long size = conn.getContentLength();
				raf.setLength(size); // 设置保存文件的大小
				try {
					long currentLength = 0; // 当前已下载好的文件长度
					byte[] buffer = new byte[1024 * 1024];
					int len = 0;
					while (currentLength < size && -1 != (len = is.read(buffer))) {

						if (currentLength + len > size) {
							raf.write(buffer, 0, (int) (size - currentLength));
							currentLength = size;
							return;
						} else {
							raf.write(buffer, 0, len);
							currentLength += len;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
						raf.close();
						conn.disconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else {
				System.out.println("no++");
			}

		} catch (Exception e) {

		}

	}
	private static String genRSASign(JSONObject reqObj){
		return addSignRSA(reqObj,TRADER_PRI_KEY);
	}
	/**
     * RSA加签名
     * 
     * @param reqObj
     * @param rsa_private
     * @return
     */
    private static String addSignRSA(JSONObject reqObj, String rsa_private)
    {
        if (reqObj == null)
        {
            return "";
        }
        // 生成待签名串
        String sign_src = genSignData(reqObj);
        try
        {
            return TraderRSAUtil.sign(rsa_private, sign_src);
        } catch (Exception e)
        {
            return "";
        }
    }
	/**
	 * 生成待签名串
	 * 
	 * @param paramMap
	 * @return
	 */
	public static String genSignData(JSONObject jsonObject) {
		StringBuffer content = new StringBuffer();

		// 按照key做首字母升序排列
		List<String> keys = new ArrayList<String>(jsonObject.keySet());
		Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			// sign 和ip_client 不参与签名
			if ("sign".equals(key)) {
				continue;
			}
			String value = (String) jsonObject.getString(key);
			// 空串不参与签名
			if (FuncUtils.isNull(value)) {
				continue;
			}
			content.append((i == 0 ? "" : "&") + key + "=" + value);

		}
		String signSrc = content.toString();
		if (signSrc.startsWith("&")) {
			signSrc = signSrc.replaceFirst("&", "");
		}
		return signSrc;
	}
}
