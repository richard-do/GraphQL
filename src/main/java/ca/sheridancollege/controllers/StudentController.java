package ca.sheridancollege.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.sheridancollege.beans.Student;
import ca.sheridancollege.repositories.StudentRepository;
import ca.sheridancollege.services.GraphQLService;
import graphql.ExecutionResult;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/students")
@AllArgsConstructor
public class StudentController {

	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	GraphQLService graphQLService;
	
	// returns the results from the graphql queries
	@GetMapping
	public ResponseEntity<Object> executeQuery (@RequestBody String query){
		ExecutionResult result = graphQLService.graphQL().execute(query);
		return new ResponseEntity<Object>(result, HttpStatus.OK);
	}
	
	// add individual students
	@PostMapping(consumes = "application/json")
	public Student postStudent(@RequestBody Student student) {
		return studentRepository.save(student);
	}
	
	// add list of students
	@PutMapping(consumes = "application/json")
	public String putStudentCollection(@RequestBody List<Student> studentList) {
		studentRepository.saveAll(studentList);
		return "Total Records: " + studentRepository.count();
	}

	// get list of students (json)
//	@GetMapping
//	public List<Student> getStudentCollection(){
//		return studentRepository.findAll();
//	}
}
