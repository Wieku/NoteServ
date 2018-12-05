package me.wieku.noteserv;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NoteservApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void noteCreationAndGet_Successful() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("foo")).andExpect(jsonPath("$.content").value("bar"));
    }

    @Test
    public void noteCreationAndGet_Date_Match() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        String response = mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andReturn().getResponse().getContentAsString();
        assertEquals(JsonPath.parse(response).read("$.dateCreated").toString(), JsonPath.parse(response).read("$.dateModified").toString());
    }

    @Test
    public void noteCreation_MissingParams_BadRequest() throws Exception {
        mockMvc.perform(post("/notes")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/notes").param("title", "foo")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/notes").param("content", "bar")).andExpect(status().isBadRequest());
    }

    @Test
    public void noteCreation_EmptyParams_BadRequest() throws Exception {
        mockMvc.perform(post("/notes").param("title", "foo").param("content", "")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/notes").param("title", "").param("content", "bar")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/notes").param("title", "").param("content", "")).andExpect(status().isBadRequest());
    }

    @Test
    public void noteCreationAndUpdate_Successful() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("foo")).andExpect(jsonPath("$.content").value("bar"));

        MvcResult result1 = mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "foo1").param("content", "bar1")).andExpect(status().isNoContent()).andReturn();
        mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("foo1")).andExpect(jsonPath("$.content").value("bar1"));
    }

    @Test
    public void noteCreationAndUpdate_MissingParams_BadRequest() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("foo")).andExpect(jsonPath("$.content").value("bar"));

        mockMvc.perform(put(result.getResponse().getRedirectedUrl())).andExpect(status().isBadRequest());
        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "foo1")).andExpect(status().isBadRequest());
        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("content", "bar1")).andExpect(status().isBadRequest());
    }

    @Test
    public void noteCreationAndUpdate_EmptyParams_BadRequest() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("foo")).andExpect(jsonPath("$.content").value("bar"));

        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "").param("content", "")).andExpect(status().isBadRequest());
        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "foo1").param("content", "")).andExpect(status().isBadRequest());
        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "").param("content", "bar1")).andExpect(status().isBadRequest());
    }

    @Test
    public void noteCreationAndUpdate_HistoryCount_2() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("foo")).andExpect(jsonPath("$.content").value("bar"));

        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "foo1").param("content", "bar1")).andExpect(status().isNoContent());
        mockMvc.perform(get(result.getResponse().getRedirectedUrl().concat("/history"))).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void noteCreationAndUpdate_History_Correct() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("foo")).andExpect(jsonPath("$.content").value("bar"));

        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "foo1").param("content", "bar1")).andExpect(status().isNoContent());
        mockMvc.perform(get(result.getResponse().getRedirectedUrl().concat("/history")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].title").value("foo"))
                .andExpect(jsonPath("$.[0].content").value("bar"))
                .andExpect(jsonPath("$.[1].title").value("foo1"))
                .andExpect(jsonPath("$.[1].content").value("bar1"));
    }

}
