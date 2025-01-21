package progi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import progi.data.BuddyRequest;

@Repository
public interface BuddyRequestRepository extends JpaRepository<BuddyRequest, String> {}
