package com.dsd.rdstore.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.dsd.rdstore.model.RefreshSession;
import jakarta.persistence.LockModeType;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, Long> {
    /** Serializa el uso del refresh token para impedir dos rotaciones simultáneas. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RefreshSession r WHERE r.tokenHash = :hash")
    Optional<RefreshSession> findByHashForUpdate(@Param("hash") String hash);
}
