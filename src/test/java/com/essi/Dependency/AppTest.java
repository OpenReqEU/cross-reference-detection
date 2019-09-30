package com.essi.Dependency;

import com.essi.dependency.Application;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit test for simple App.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppTest {

    @Autowired
    private MockMvc mockMvc;

    private String path = "./testing/integration/";

    @Test
    public void crossReferenceJSONProject() throws Exception {
        String projectId = "QTBUG";
        String response = this.mockMvc.perform(
                post("/upc/cross-reference-detection/json/" + projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(read_file(path+"sample-1.json")))
                .andDo(print()).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JSONObject result = new JSONObject(response);
        Assert.assertEquals(1, result.getJSONArray("dependencies").length());
    }

    @Test
    public void crossReferenceJSONProjectNM() throws Exception {
        String projectId = "QTBUG";
        String response = this.mockMvc.perform(
                post("/upc/cross-reference-detection/json/" + projectId + "/1/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(read_file(path+"sample-1.json")))
                .andDo(print()).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JSONObject result = new JSONObject(response);
        Assert.assertEquals(1, result.getJSONArray("dependencies").length());
    }

    @Test
    public void crossReferenceHTMLProject() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file","sample-2.html",
                "text/html", read_html_file(path+"sample-2.html").getBytes());

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.fileUpload("/upc/cross-reference-detection/file")
                        .file(mockMultipartFile);

        String response = this.mockMvc.perform(builder).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn().getResponse().getContentAsString();;
        JSONObject result = new JSONObject(response);
        Assert.assertEquals(9, result.getJSONArray("dependencies").length());
    }

    @Test
    public void crossReferenceHTMLProjectNM() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file","sample-2.html",
                "text/html", read_html_file(path+"sample-2.html").getBytes());

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.fileUpload("/upc/cross-reference-detection/file/1/2")
                        .file(mockMultipartFile);

        String response = this.mockMvc.perform(builder).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn().getResponse().getContentAsString();;
        JSONObject result = new JSONObject(response);
        Assert.assertEquals(0, result.getJSONArray("dependencies").length());
    }

    private String read_html_file(String path) throws Exception {
        String result = "";
        String line = "";
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(path);
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                result = result.concat(line);
            }
            bufferedReader.close();
            return result;
        } finally {
            if (fileReader != null) fileReader.close();
            if (bufferedReader != null) bufferedReader.close();
        }
    }

    private String read_file(String path) throws Exception {
        String result = "";
        String line = "";
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(path);
            bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                result = result.concat(line);
            }
            bufferedReader.close();
            JSONObject aux = new JSONObject(result);
            return aux.toString();
        } finally {
            if (fileReader != null) fileReader.close();
            if (bufferedReader != null) bufferedReader.close();
        }
    }


}
