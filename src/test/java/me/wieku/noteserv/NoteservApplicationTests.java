package me.wieku.noteserv;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

}
