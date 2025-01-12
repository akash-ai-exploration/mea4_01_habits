package ch.bfh.habits.services

import ch.bfh.habits.dtos.habit.*
import ch.bfh.habits.entities.Habit
import ch.bfh.habits.entities.JournalEntry
import ch.bfh.habits.exceptions.BadRequestException
import ch.bfh.habits.exceptions.EntityNotFoundException
import ch.bfh.habits.repositories.HabitDAO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HabitService(private val h: HabitDAO, private val g: GroupService) {

    @Transactional fun a(i: Long, u: Long) = h.findAllByGroupIdAndUserId(i, u)

    @Transactional fun b(u: Long) = h.findAllByUserId(u)

    @Transactional fun c(d: HabitDTO, u: Long): Habit {
        if ((d.frequencyValue != null && d.frequency == null) || (d.frequencyValue == null && d.frequency != null)) throw BadRequestException("Frequency and frequencyValue must be set together")
        val n = HabitEntityBuilder.createHabitEntityFromDTO(d, u)
        if (d.groupId != null) n.group = g.getGroup(d.groupId!!, u)
        return h.save(n)
    }

    @Transactional fun d(i: Long, u: Long) = h.findByUserIdAndId(u, i) ?: throw EntityNotFoundException("Habit not found or not owned by user")

    @Transactional fun e(i: Long, u: Long) = h.delete(d(i, u))

    @Transactional fun f(i: Long, d: HabitDTO, u: Long): Habit {
        if ((d.frequencyValue != null && d.frequency == null) || (d.frequencyValue == null && d.frequency != null)) throw BadRequestException("Frequency and frequencyValue must be set together")
        val c = h.findByUserIdAndId(u, i) ?: throw EntityNotFoundException("Habit not found or not owned by user")
        if (c.group?.id != d.groupId) c.group = if (d.groupId != null) g.getGroup(d.groupId!!, u) else null
        HabitEntityBuilder.applyHabitDtoToEntity(d, c)
        return c
    }
}
