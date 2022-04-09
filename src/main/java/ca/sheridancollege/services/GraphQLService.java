package ca.sheridancollege.services;

import java.io.File;
import java.io.IOException;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import ca.sheridancollege.repositories.StudentRepository;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@Service
public class GraphQLService {

	@Autowired
	StudentRepository studentRepository;
	
	@Value("classpath:schema.graphqls")
	private Resource schemaResource;
	
	private GraphQL graphQL;
	
	// constructs our graphql object based on the schema provided
	// alongside our wiring
	@PostConstruct
	public void init() throws IOException {
		File schemaFile = schemaResource.getFile();
		TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaFile);
		RuntimeWiring runtimeWiring = buildWiring();
		SchemaGenerator schemaGenerator = new SchemaGenerator();
		GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
		this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
	}
	
	// wires the type to the data fetchers
	private RuntimeWiring buildWiring() {
		return RuntimeWiring.newRuntimeWiring()
				.type("Query", typeWiring -> typeWiring
						.dataFetcher("getStudentById", getStudentByIdFetcher())
						.dataFetcher("getStudents", getStudentsFetcher()))
				.build();
	}
	
	// get student by id data fetcher
	public DataFetcher getStudentByIdFetcher() {
		return dataFetcher -> {
			String studentId = dataFetcher.getArgument("id");
			return studentRepository
					.findAll()
					.stream()
					.filter(student -> student.getId().equals(Long.parseLong(studentId)))
					.findFirst()
					.orElse(null);
		};
	}
	
	// get all students data fetcher
	public DataFetcher getStudentsFetcher() {
		return dataFetcher -> {
			return studentRepository.findAll();
		};
	}
	
	public GraphQL graphQL() {
		return this.graphQL;
	}
}
