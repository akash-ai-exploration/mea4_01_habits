package ch.bfh.habits.controllers

import ch.bfh.habits.dtos.user.JwtTokenDTO
import ch.bfh.habits.dtos.user.LoginDTO
import ch.bfh.habits.dtos.user.RegisterDTO
import ch.bfh.habits.dtos.user.UserEntityBuilder
import ch.bfh.habits.entities.User
import ch.bfh.habits.exceptions.UnauthorizedException
import ch.bfh.habits.services.UserService
import ch.bfh.habits.util.TokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.servlet.http.HttpServletResponse

@RestController
class AuthController {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var tokenProvider: TokenProvider

    @Autowired
    lateinit var userService: UserService

    @PostMapping("api/register")
    fun register(@RequestBody body: RegisterDTO?): ResponseEntity<User?> {
        println("Received registration request for user: ${body?.userName}")
        val user = UserEntityBuilder.createUserEntityFromDTO(body!!)
        val savedUser = userService.save(user)
        println("User registered: ${savedUser.userName}")
        return ResponseEntity.status(200).body(savedUser)
    }

    @PostMapping("api/login")
    fun login(@RequestBody body: LoginDTO?): ResponseEntity<Any?> {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(body?.userName, body?.password)
            )
        } catch (e: Exception) {
            println("Error during login: ${e.message}")
            return ResponseEntity.status(401).body("Login failed")
        }

        val userDetails = userService.loadUserByUsername(body!!.userName)
        val user = userService.findByUserName(body.userName)
        val jwt = tokenProvider.generateToken(userDetails, user!!)

        println("Token generated for user: ${user.userName}")
        return ResponseEntity.ok(jwt)
    }

    @GetMapping("api/user")
    fun user(@RequestHeader(value = "Authorization") token: String?): ResponseEntity<Any?> {
        if (token == null) return ResponseEntity.status(400).body("Missing token")

        val userName = tokenProvider.extractUsername(token)
        if (userName.isNullOrEmpty()) return ResponseEntity.status(401).body("Invalid token")

        val user = userService.findByUserName(userName)
        println("Fetched user details: $user")
        return ResponseEntity.ok(user)
    }

    @DeleteMapping("api/user/{userName}")
    fun deleteUser(@PathVariable userName: String): ResponseEntity<Any?> {
        return try {
            userService.deleteByUserName(userName)
            println("User deleted: $userName")
            ResponseEntity.status(200).body("User deleted successfully")
        } catch (e: Exception) {
            println("Error deleting user: ${e.message}")
            ResponseEntity.status(500).body("Error deleting user")
        }
    }
}
