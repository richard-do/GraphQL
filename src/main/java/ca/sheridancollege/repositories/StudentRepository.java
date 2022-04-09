package ca.sheridancollege.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.sheridancollege.beans.Student;

public interface StudentRepository extends JpaRepository <Student, Long> {

	
	
}
