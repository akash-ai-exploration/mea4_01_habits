package ch.bfh.habits.controllers;import ch.bfh.habits.entities.Group;import ch.bfh.habits.exceptions.EntityNotFoundException;import ch.bfh.habits.services.GroupService;import ch.bfh.habits.services.UserService;import ch.bfh.habits.util.TokenProvider;import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper;import io.mockk.every;import io.mockk.mockk;import io.mockk.verify;import org.junit.jupiter.api.Assertions;import org.junit.jupiter.api.Test;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;import org.springframework.boot.test.context.TestConfiguration;import org.springframework.context.annotation.Bean;import org.springframework.security.core.context.SecurityContextHolder;import org.springframework.security.test.context.support.WithMockUser;import org.springframework.test.web.servlet.MockMvc;import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(GroupController::class)
public class GroupControllerTests {
    @TestConfiguration
    public class GroupControllerTestsConfig {
        @Bean
        public fun service() = mockk<GroupService>()
        @Bean
        public fun tokenProvider() = mockk<TokenProvider> {
            every { validateToken(any(), any()) } returns true
            every { extractUsername("correctUser") } returns "correctUser"
            every { extractUsername("wrongUser") } returns "wrongUser"
            every { extractId("correctUser") } returns 1
            every { extractId("wrongUser") } returns 2
        }
        @Bean
        public fun userService() = mockk<UserService>()
    }

    @Autowired
    public var mockMvc: MockMvc? = null

    @Autowired
    public var service: GroupService? = null

    public var mapper = jacksonObjectMapper()

    @Test
    @WithMockUser(username = "correctUser")
    public fun getAllGroupsReturnTheCurrentUsersGroups() {
        var group = Group("Private",1, userId = 1)
        every { service!!.getAllGroups(1) } returns arrayListOf(group)
        var userName = SecurityContextHolder.getContext().authentication.name

        var result = mockMvc!!.perform(MockMvcRequestBuilders.get("/api/groups").header("Authorization", userName))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        Assertions.assertEquals(mapper.writeValueAsString(arrayListOf(group)), result.response.contentAsString)
        verify { service!!.getAllGroups(1) }
    }

    @Test
    @WithMockUser(username = "correctUser")
    public fun getHabitReturnTheRequestedGroup() {
        var group = Group("Private",1, userId = 1)
        every { service!!.getGroup(1, 1) } returns group
        var userName = SecurityContextHolder.getContext().authentication.name

        var result = mockMvc!!.perform(MockMvcRequestBuilders.get("/api/group/1").header("Authorization", userName))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        Assertions.assertEquals(mapper.writeValueAsString(group), result.response.contentAsString)
        verify { service!!.getGroup(1, 1) }
    }

    @Test
    @WithMockUser(username = "wrongUser")
    public fun getGroupReturnsNotFoundIfUserIsNotAllowedToAccessGroup() {
        var group = Group("Private", 1, userId = 1)
        every { service!!.getGroup(1, 1) } returns group
        every { service!!.getGroup(1, 2) } throws EntityNotFoundException("")
        var userName = SecurityContextHolder.getContext().authentication.name

        mockMvc!!.perform(MockMvcRequestBuilders.get("/api/group/1").header("Authorization", userName))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andReturn()

        verify { service!!.getGroup(1, 2) }
    }

    @Test
    @WithMockUser(username = "correctUser")
    public fun getGroupReturnsNotFoundIfGroupDoesNotExist() {
        var group = Group("Private", 1, userId = 1)
        every { service!!.getGroup(1, 1) } returns group
        every { service!!.getGroup(2, 1) } throws EntityNotFoundException("")
        var userName = SecurityContextHolder.getContext().authentication.name

        mockMvc!!.perform(MockMvcRequestBuilders.get("/api/group/2").header("Authorization", userName))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andReturn()

        verify { service!!.getGroup(2, 1) }
    }
}
