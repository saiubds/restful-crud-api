package com.sai.app.restfulcrudapi.Repo;

import com.sai.app.restfulcrudapi.Models.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepo extends JpaRepository<Ticket, Long> {
}
