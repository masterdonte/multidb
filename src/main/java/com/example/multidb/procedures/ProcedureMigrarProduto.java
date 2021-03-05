package com.example.multidb.procedures;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

@Component
public class ProcedureMigrarProduto {

	@Autowired
	@Qualifier("jdbcTemplateC")
	private JdbcTemplate jdbcTemplate;
	private SimpleJdbcCall simpleJdbcCall;
	private static final String PROC_NAME = NameGenerator.randomName(30);
	
	//private static final String PROC_NAME = "MIGRAR_PRODUTO_VEICULO_SAIDO";	
	private static final String SQL_CREATE_PROC = ""
			+ "CREATE OR REPLACE PROCEDURE " + PROC_NAME +"(P_PLACA IN VARCHAR2, P_TICKET IN NUMBER, P_OPERJANORIGEMID IN NUMBER,P_OPERJANDESTINOID IN NUMBER) AS" + "\n"
			+ "" + "\n"
			+ "    V_MERCADORIAORIGEMID  NUMBER(10);" + "\n"
			+ "    V_MERCADORIADESTINOID NUMBER(10);" + "\n"
			+ "    V_TRANSPORTEMOVID     NUMBER(10);" + "\n"
			+ "    V_PESAGEMMERCADORIAID NUMBER(10);" + "\n"
			+ "    " + "\n"
			+ "    V_QTD_ENTRADO_ORIGEM  NUMBER(10);" + "\n"
			+ "    V_QTD_SAIDO_ORIGEM    NUMBER(10);" + "\n"
			+ "    V_QTD_ENTRADO_DESTINO NUMBER(10);" + "\n"
			+ "    V_QTD_SAIDO_DESTINO   NUMBER(10);" + "\n"
			+ "" + "\n"
			+ "BEGIN" + "\n"
			+ "" + "\n"
			+ "    SELECT B.VEICULOMOVIMENTOID, B.CODIGO INTO V_TRANSPORTEMOVID, V_PESAGEMMERCADORIAID FROM BILHETEPESAGEM B WHERE B.PLACA = P_PLACA AND B.TICKET = P_TICKET;    " + "\n"
			+ "    SELECT OP.MERCADORIAID INTO V_MERCADORIAORIGEMID  FROM NUCLEUS.OPERACAOJANELA OP WHERE OP.ID = P_OPERJANORIGEMID;" + "\n"
			+ "    SELECT OP.MERCADORIAID INTO V_MERCADORIADESTINOID FROM NUCLEUS.OPERACAOJANELA OP WHERE OP.ID = P_OPERJANDESTINOID;" + "\n"
			+ "    " + "\n"
			+ "    UPDATE NUCLEUS.FLUXOMERCADORIA SET MERCADORIAID = V_MERCADORIADESTINOID WHERE ITEMFLUXOAGREGADOID IN (" + "\n"
			+ "           SELECT ID FROM NUCLEUS.ITEMFLUXOAGREGADO ITFA WHERE ITFA.TRANSPORTEMOVIMENTOID = V_TRANSPORTEMOVID" + "\n"
			+ "    );	" + "\n"
			+ "    UPDATE NUCLEUS.FLUXOES SET MERCADORIAID = V_MERCADORIADESTINOID WHERE TRANSPORTEMOVIMENTOIDTRA   = V_TRANSPORTEMOVID AND MERCADORIAID = V_MERCADORIAORIGEMID;" + "\n"
			+ "    UPDATE NUCLEUS.FLUXOES SET OPERACAOJANELAID = P_OPERJANDESTINOID WHERE TRANSPORTEMOVIMENTOIDTRA  = V_TRANSPORTEMOVID AND MODAL = 1;" + "\n"
			+ "    UPDATE NUCLEUS.MOVIMENTACAO SET MERCADORIAID = V_MERCADORIADESTINOID WHERE TRANSPORTEMOVIMENTOID = V_TRANSPORTEMOVID;" + "\n"
			+ "    UPDATE NUCLEUS.PESAGEMMERCADORIA SET MERCADORIAID = V_MERCADORIADESTINOID WHERE ID = V_PESAGEMMERCADORIAID;" + "\n"
			+ "    " + "\n"
			+ "    /*ATUALIZANDO A DADOS DA JANELA DE ORIGEM*/" + "\n"
			+ "    SELECT SUM(QTD) INTO V_QTD_ENTRADO_ORIGEM FROM NUCLEUS.MOVIMENTACAO WHERE MERCADORIAID = V_MERCADORIAORIGEMID AND ENDERECO = '1IN';" + "\n"
			+ "    SELECT SUM(QTD) INTO V_QTD_SAIDO_ORIGEM   FROM NUCLEUS.MOVIMENTACAO WHERE MERCADORIAID = V_MERCADORIAORIGEMID AND ENDERECO = '1OUT';    " + "\n"
			+ "    UPDATE NUCLEUS.OPERACAOJANELA SET QTDREALIZADA = V_QTD_ENTRADO_ORIGEM WHERE ID = P_OPERJANORIGEMID;" + "\n"
			+ "    UPDATE NUCLEUS.MERCADORIA SET PLR = V_QTD_ENTRADO_ORIGEM, QTDENTRADO = V_QTD_ENTRADO_ORIGEM, QTDSAIDO = V_QTD_SAIDO_ORIGEM WHERE ID = V_MERCADORIAORIGEMID;  " + "\n"
			+ "    " + "\n"
			+ "    /*ATUALIZANDO A DADOS DA JANELA DE ORIGEM*/" + "\n"
			+ "    SELECT SUM(QTD) INTO V_QTD_ENTRADO_DESTINO FROM NUCLEUS.MOVIMENTACAO WHERE MERCADORIAID = V_MERCADORIADESTINOID AND ENDERECO = '1IN';" + "\n"
			+ "    SELECT SUM(QTD) INTO V_QTD_SAIDO_DESTINO   FROM NUCLEUS.MOVIMENTACAO WHERE MERCADORIAID = V_MERCADORIADESTINOID AND ENDERECO = '1OUT';    " + "\n"
			+ "    UPDATE NUCLEUS.OPERACAOJANELA SET QTDREALIZADA = V_QTD_ENTRADO_DESTINO WHERE ID = P_OPERJANDESTINOID;" + "\n"
			+ "    UPDATE NUCLEUS.MERCADORIA SET PLR = V_QTD_ENTRADO_DESTINO, QTDENTRADO = V_QTD_ENTRADO_DESTINO, QTDSAIDO = V_QTD_SAIDO_DESTINO WHERE ID = V_MERCADORIADESTINOID;" + "\n" 
			+ "  " + "\n"
			+ "END " + PROC_NAME + ";\n";

	private static final String SQL_DROP_PROC = "DROP PROCEDURE " + PROC_NAME;

	public void execute(List<Migracao> migracoes, Long opjOriId, Long opjDesId){    	
		try {
			init();
			createProcedure();
			
			
			for(Migracao mi : migracoes) {
				executeProcedure(mi.getPlaca(), mi.getTicket(), opjOriId, opjDesId);
			}
			
			//migracoes.forEach(System.out::println);
			
			dropProcedure();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

	}

	private void init() {
		jdbcTemplate.setResultsMapCaseInsensitive(true);
		simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(PROC_NAME);
	}

	void createProcedure() throws Exception {
		System.out.println("Creating procedure " + PROC_NAME);
		jdbcTemplate.execute(SQL_CREATE_PROC);
	}

	private void executeProcedure(String placa, Long nroTicket, Long opjOriId, Long opjDesId) throws Exception{		
	
		SqlParameterSource paramsIn = new MapSqlParameterSource()
				.addValue("P_PLACA", 			 placa)
				.addValue("P_TICKET",   		 nroTicket)
				.addValue("P_OPERJANORIGEMID",   opjOriId)
				.addValue("P_OPERJANDESTINOID",  opjDesId);
		try {
			simpleJdbcCall.execute(paramsIn);
		} catch (Exception e) {            
			System.err.println(e.getMessage()); // ORA-01403: no data found, or any java.sql.SQLException
		}		
	}

	private void dropProcedure() throws Exception {
		System.out.println("Dropping procedure "+ PROC_NAME );
		jdbcTemplate.execute(SQL_DROP_PROC);
	}
}
