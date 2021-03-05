package com.example.multidb;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.multidb.procedures.Migracao;
import com.example.multidb.procedures.ProcedureMigrarProduto;
import com.example.multidb.procedures.ProcedureMudarPesagem;

@SpringBootApplication
public class MultidbApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(MultidbApplication.class, args);
	}

	//@Autowired private CategoriaRepository banco1;
	
	//@Autowired private TurmaRepository banco2;
	
	//@Autowired private SalaRepository banco3;
	
	//@Autowired private LivroRepository banco4;
	
	@Autowired
    ProcedureMudarPesagem procedureMudarPesagem;
    
    @Autowired
    ProcedureMigrarProduto procedureMigrarProduto;

	@Override
	public void run(String... args) throws Exception {
		
		/*System.out.println("Rodou");
		System.out.println("==========CATEGORIAS=====================");
		banco1.findAll().forEach(System.out::println);
		System.out.println("===========TURMAS========================");
		banco2.findAll().forEach(System.out::println);*/
		
		//banco1.save(new Categoria(NameGenerator.randomName(8)));
		//banco2.save(new Turma(NameGenerator.randomName(8)));
/*		
		banco3.save(new Sala(NameGenerator.randomName(8)));
		System.out.println("===========TURMAS========================");
		banco3.findAll().forEach(System.out::println);
	*/	
	//	System.out.println("===========LIVROS========================");
		
		//banco4.save(new Livro(NameGenerator.randomName(8)));
		//banco4.findAll().forEach(System.out::println);
		
		//procedureMudarPesagem.start(367108, 17740,  50540);      
	    executarMigracaoProduto();
	}
	    
	//@SuppressWarnings("unused")
	private void executarMigracaoProduto() {
    	List<Migracao> itens = new ArrayList<Migracao>();    	
        	
    	/*itens.add(new Migracao("NWS8622", 448058L));   
    	itens.add(new Migracao("MUA2H89", 448056L));   
    	itens.add(new Migracao("KOY1394", 448043L));   
    	itens.add(new Migracao("PTO0992", 447855L));
    	    	
     	procedureMigrarProduto.execute(itens, 11296L, 11298L);*/
     
     	itens.add(new Migracao("JVA9367", 439383L));
     	itens.add(new Migracao("MUA2H89", 439340L));
     	procedureMigrarProduto.execute(itens, 11194L, 11193L);
    }

}