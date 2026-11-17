package chnu.edu.websecuritylabs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemAccessTests {

    private static final String URL = "/api/v1/items";

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("1. GET all items - anonymous -> 401")
    void getAllAnonymousUnauthorized() throws Exception {
        mvc.perform(get(URL))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("2. GET all items - USER -> 200")
    @WithMockUser(roles = "USER")
    void getAllAsUserOk() throws Exception {
        mvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("3. GET one item - ADMIN via httpBasic -> 200")
    void getOneAsAdminBasicOk() throws Exception {
        mvc.perform(get(URL + "/1").with(httpBasic("admin", "admin")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"));
    }

    @Test
    @DisplayName("4. GET one item - wrong password -> 401")
    void getOneWrongPasswordUnauthorized() throws Exception {
        mvc.perform(get(URL + "/1").with(httpBasic("user", "wrong")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("5. POST item - USER -> 403")
    @WithMockUser(roles = "USER")
    void createAsUserForbidden() throws Exception {
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"10\",\"name\":\"name10\",\"description\":\"description10\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("6. POST item - MANAGER -> 200")
    @WithMockUser(roles = "MANAGER")
    void createAsManagerOk() throws Exception {
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"11\",\"name\":\"name11\",\"description\":\"description11\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("11"));
    }

    @Test
    @DisplayName("7. PUT item - USER -> 403")
    @WithMockUser(roles = "USER")
    void updateAsUserForbidden() throws Exception {
        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"2\",\"name\":\"name22\",\"description\":\"description22\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("8. PUT item - ADMIN -> 200")
    @WithMockUser(roles = "ADMIN")
    void updateAsAdminOk() throws Exception {
        mvc.perform(put(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"2\",\"name\":\"name22\",\"description\":\"description22\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name22"));
    }

    @Test
    @DisplayName("9. DELETE item - MANAGER -> 403")
    @WithMockUser(roles = "MANAGER")
    void deleteAsManagerForbidden() throws Exception {
        mvc.perform(delete(URL + "/3"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("10. DELETE item - ADMIN -> 200")
    @WithMockUser(roles = "ADMIN")
    void deleteAsAdminOk() throws Exception {
        mvc.perform(delete(URL + "/3"))
                .andExpect(status().isOk());
    }
}
