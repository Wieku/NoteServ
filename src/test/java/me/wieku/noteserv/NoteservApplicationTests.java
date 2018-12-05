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

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NoteservApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldCreateNote() throws Exception {
        mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated());
    }

    @Test
    public void shouldCreateAndGetNote() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("foo")).andExpect(jsonPath("$.content").value("bar"));
    }

    @Test
    public void shouldDatesOfFreshlyCreatedNoteMatch() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        String response = mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andReturn().getResponse().getContentAsString();
        assertEquals(JsonPath.parse(response).read("$.dateCreated").toString(), JsonPath.parse(response).read("$.dateModified").toString());
    }

    @Test
    public void shouldReturnBadRequestOnMissingParamsOnPostRequest() throws Exception {
        mockMvc.perform(post("/notes")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/notes").param("title", "foo")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/notes").param("content", "bar")).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestOnEmptyParamsOnPostRequest() throws Exception {
        mockMvc.perform(post("/notes").param("title", "foo").param("content", "")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/notes").param("title", "").param("content", "bar")).andExpect(status().isBadRequest());
        mockMvc.perform(post("/notes").param("title", "").param("content", "")).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldCreateAndUpdateNote() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("foo")).andExpect(jsonPath("$.content").value("bar"));

        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "foo1").param("content", "bar1")).andExpect(status().isNoContent());
        mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("foo1")).andExpect(jsonPath("$.content").value("bar1"));
    }

    @Test
    public void shouldReturnBadRequestOnMissingParamsOnUpdateRequest() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("foo")).andExpect(jsonPath("$.content").value("bar"));

        mockMvc.perform(put(result.getResponse().getRedirectedUrl())).andExpect(status().isBadRequest());
        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "foo1")).andExpect(status().isBadRequest());
        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("content", "bar1")).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestOnEmptyParamsOnUpdateRequest() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("foo")).andExpect(jsonPath("$.content").value("bar"));

        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "").param("content", "")).andExpect(status().isBadRequest());
        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "foo1").param("content", "")).andExpect(status().isBadRequest());
        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "").param("content", "bar1")).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldHistoryLengthBeTwoOnOneUpdate() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andExpect(status().isOk()).andExpect(jsonPath("$.title").value("foo")).andExpect(jsonPath("$.content").value("bar"));

        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "foo1").param("content", "bar1")).andExpect(status().isNoContent());
        mockMvc.perform(get(result.getResponse().getRedirectedUrl().concat("/history"))).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void shouldHistoryOfUpdatedNoteBeCorrect() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();

        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "foo1").param("content", "bar1")).andExpect(status().isNoContent());
        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "2foo").param("content", "2bar")).andExpect(status().isNoContent());

        mockMvc.perform(get(result.getResponse().getRedirectedUrl().concat("/history")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].title").value("foo"))
                .andExpect(jsonPath("$.[0].content").value("bar"))
                .andExpect(jsonPath("$.[1].title").value("foo1"))
                .andExpect(jsonPath("$.[1].content").value("bar1"))
                .andExpect(jsonPath("$.[2].title").value("2foo"))
                .andExpect(jsonPath("$.[2].content").value("2bar"));
    }

    @Test
    public void shouldDeleteNote() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(delete(result.getResponse().getRedirectedUrl())).andExpect(status().isOk());
    }

    @Test
    public void shouldHistoryOfDeletedNoteExist() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(delete(result.getResponse().getRedirectedUrl())).andExpect(status().isOk());

        mockMvc.perform(get(result.getResponse().getRedirectedUrl().concat("/history"))).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void shouldNotFoundDeletedNoteOnGetRequest() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(delete(result.getResponse().getRedirectedUrl())).andExpect(status().isOk());

        mockMvc.perform(get(result.getResponse().getRedirectedUrl())).andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotFoundDeletedNoteOnGetAllRequest() throws Exception {
        int length = JsonPath.parse(mockMvc.perform(get("/notes")).andReturn().getResponse().getContentAsString()).read("$.length()");
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(delete(result.getResponse().getRedirectedUrl())).andExpect(status().isOk());

        mockMvc.perform(get("/notes")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(length));
    }

    @Test
    public void shouldNotFoundTheRevisionOfTheDeletedNote() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(delete(result.getResponse().getRedirectedUrl())).andExpect(status().isOk());

        mockMvc.perform(get(result.getResponse().getRedirectedUrl().concat("/0"))).andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotFoundDeletedNoteOnUpdateRequest() throws Exception {
        MvcResult result = mockMvc.perform(post("/notes").param("title", "foo").param("content", "bar")).andExpect(status().isCreated()).andReturn();
        mockMvc.perform(delete(result.getResponse().getRedirectedUrl())).andExpect(status().isOk());
        mockMvc.perform(put(result.getResponse().getRedirectedUrl()).param("title", "foo1").param("content", "bar1")).andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotFoundNonexistentNoteOnDeleteRequest() throws Exception {
        UUID uuid = UUID.randomUUID();
        mockMvc.perform(delete("/notes/{noteId}", uuid)).andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotFoundNonexistentNote() throws Exception {
        UUID uuid = UUID.randomUUID();
        mockMvc.perform(get("/notes/{noteId}", uuid)).andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotFoundTheRevisionOfNonexistentNote() throws Exception {
        UUID uuid = UUID.randomUUID();
        mockMvc.perform(get("/notes/{noteId}/0", uuid)).andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotFoundNonexistentNoteOnUpdateRequest() throws Exception {
        UUID uuid = UUID.randomUUID();
        mockMvc.perform(put("/notes/{noteId}", uuid).param("title", "foo1").param("content", "bar1")).andExpect(status().isNotFound());
    }

    @Test
    public void shouldNotFoundTheHistoryOfNonexistentNote() throws Exception {
        UUID uuid = UUID.randomUUID();
        mockMvc.perform(get("/notes/{noteId}/history", uuid)).andExpect(status().isNotFound());
    }

}
