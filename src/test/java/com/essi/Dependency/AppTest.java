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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        Assert.assertEquals(2, result.getJSONArray("dependencies").length());
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
        Assert.assertEquals(2, result.getJSONArray("dependencies").length());
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
        Assert.assertEquals(19, result.getJSONArray("dependencies").length());
    }

    @Test
    public void crossReferenceHTMLProjectComplex() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file","complex.html",
                "text/html", read_html_file(path+"complex.html").getBytes());

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.fileUpload("/upc/cross-reference-detection/file")
                        .file(mockMultipartFile);

        String response = this.mockMvc.perform(builder).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn().getResponse().getContentAsString();;
        JSONObject result = new JSONObject(response);
        Assert.assertEquals(3, result.getJSONArray("dependencies").length());
    }

    @Test
    public void crossReferenceHTMLProjectExternal() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file","external.html",
                "text/html", read_html_file(path+"external.html").getBytes());

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.fileUpload("/upc/cross-reference-detection/file")
                        .file(mockMultipartFile);

        String response = this.mockMvc.perform(builder).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn().getResponse().getContentAsString();;
        JSONObject result = new JSONObject(response);
        Assert.assertEquals(4, result.getJSONArray("dependencies").length());
    }


    @Test
    public void crossReferenceHTMLProjectWithGrammar() throws Exception {
        this.mockMvc.perform(
                post("/upc/cross-reference-detection/reqPrefix?company=upc_new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(read_file(path+"prefixes.json")))
                .andDo(print()).andExpect(status().isOk());
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file","sample-2.html",
                "text/html", read_html_file(path+"sample-2.html").getBytes());

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.fileUpload("/upc/cross-reference-detection/file?company=upc_new")
                        .file(mockMultipartFile);

        String response = this.mockMvc.perform(builder).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn().getResponse().getContentAsString();;
        JSONObject result = new JSONObject(response);
        Assert.assertEquals(19, result.getJSONArray("dependencies").length());
        this.mockMvc.perform(
                delete("/upc/cross-reference-detection/reqPrefix?company=upc_new"))
                .andDo(print()).andExpect(status().isOk());
    }


    @Test
    public void crossReferenceHTMLProjectException() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file","aux_exception.txt",
                "text/html", read_html_file(path+"aux_exception.txt").getBytes());

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.fileUpload("/upc/cross-reference-detection/file")
                        .file(mockMultipartFile);

        this.mockMvc.perform(builder).andExpect(status().isInternalServerError());
    }

    @Test
    public void crossReferenceHTMLProjectNM() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file","sample-2.html",
                "text/html", read_html_file(path+"sample-2.html").getBytes());

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.fileUpload("/upc/cross-reference-detection/file/1/2")
                        .file(mockMultipartFile);

        String response = this.mockMvc.perform(builder).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn().getResponse().getContentAsString();
        JSONObject result = new JSONObject(response);
        Assert.assertEquals(4, result.getJSONArray("dependencies").length());
    }

    @Test
    public void crossReferenceHTMLProjectNMException() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file","aux_exception.txt",
                "text/html", read_html_file(path+"aux_exception.txt").getBytes());
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.fileUpload("/upc/cross-reference-detection/file/1/2")
                        .file(mockMultipartFile);
        this.mockMvc.perform(builder).andExpect(status().isInternalServerError());

        mockMultipartFile = new MockMultipartFile("file","sample-2.html",
                "text/html", read_html_file(path+"sample-2.html").getBytes());
        builder =
                MockMvcRequestBuilders.fileUpload("/upc/cross-reference-detection/file/2/1")
                        .file(mockMultipartFile);
        this.mockMvc.perform(builder).andExpect(status().isInternalServerError());
    }

    @Test
    public void GrammarDeletePrefixes() throws Exception {
        this.mockMvc.perform(
                post("/upc/cross-reference-detection/reqPrefix?company=upc_new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(read_file(path+"prefixes.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(
                delete("/upc/cross-reference-detection/reqPrefix?company=upc_new"))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(
                delete("/upc/cross-reference-detection/reqPrefix?company=upc_new"))
                .andDo(print()).andExpect(status().isInternalServerError());
    }

    @Test
    public void GrammarStorePrefixes() throws Exception {
        this.mockMvc.perform(
                post("/upc/cross-reference-detection/reqPrefix?company=upc_new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(read_file(path+"prefixes.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(
                post("/upc/cross-reference-detection/reqPrefix?company=upc_new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(read_file(path+"prefixes.json")))
                .andDo(print()).andExpect(status().isInternalServerError());
        this.mockMvc.perform(
                delete("/upc/cross-reference-detection/reqPrefix?company=upc_new"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void GrammarUpdatePrefixes() throws Exception {
        this.mockMvc.perform(
                post("/upc/cross-reference-detection/reqPrefix?company=upc_new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(read_file(path+"prefixes.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(
                put("/upc/cross-reference-detection/reqPrefix?company=upc_new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(read_file(path+"prefixes.json")))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(
                delete("/upc/cross-reference-detection/reqPrefix?company=upc_new"))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(
                put("/upc/cross-reference-detection/reqPrefix?company=upc_exception")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(read_file(path+"prefixes.json")))
                .andDo(print()).andExpect(status().isInternalServerError());
    }

    @Test
    public void GrammarGetPrefixes() throws Exception {
        this.mockMvc.perform(
                post("/upc/cross-reference-detection/reqPrefix?company=upc_new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(read_file(path+"prefixes.json")))
                .andDo(print()).andExpect(status().isOk());
        String response = this.mockMvc.perform(
                get("/upc/cross-reference-detection/reqPrefix?company=upc_new"))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JSONObject result = new JSONObject(response);
        Assert.assertEquals("{\"prefixes\":[\"upc\",\"bug\",\"issue\"]}", result.toString());
        this.mockMvc.perform(
                delete("/upc/cross-reference-detection/reqPrefix?company=upc_new"))
                .andDo(print()).andExpect(status().isOk());
        this.mockMvc.perform(
                get("/upc/cross-reference-detection/reqPrefix?company=upc_exception"))
                .andDo(MockMvcResultHandlers.print()).andExpect(status().isInternalServerError()).andReturn().getResponse().getContentAsString();
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
