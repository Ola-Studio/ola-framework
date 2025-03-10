package io.ola.common.http;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.catalina.util.ParameterMap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author yiuman
 * @date 2023/7/25
 */
public class JsonHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final ParameterMap<String, String[]> parameterMap = new ParameterMap<>();
    private byte[] bytes;
    private Object parsedObject;

    public JsonHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        parseRequest(request);
    }

    private void parseRequest(HttpServletRequest request) throws IOException {
        if (request.getInputStream().available() <= 0) {
            return;
        }
        String body = IoUtil.read(request.getInputStream(), Charset.defaultCharset());
        this.bytes = body.getBytes();
        if (StrUtil.isBlank(body)) {
            return;
        }

        parsedObject = JSON.parse(bytes);
    }

    public List<?> getArray() {
        if (parsedObject instanceof JSONArray) {
            return (JSONArray) parsedObject;
        }
        return CollUtil.newArrayList();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        parameterMap.putAll(super.getParameterMap());
        if (parsedObject instanceof JSONObject) {
            ((JSONObject) parsedObject).forEach(
                    (key, value) -> parameterMap.put(key, new String[]{value.toString()})
            );
        }
        return parameterMap;
    }

    @Override
    public String getParameter(String name) {
        String[] results = getParameterMap().get(name);
        return (results == null || results.length <= 0) ? null : results[0];
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] results = getParameterMap().get(name);
        if (results == null || results.length <= 0) {
            return null;
        } else {
            return results;
        }
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Optional.ofNullable(bytes)
                .orElse(new byte[0]));
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() <= 0;
            }

            @Override
            public boolean isReady() {
                return byteArrayInputStream.available() > 0;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }

            @Override
            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }
}
