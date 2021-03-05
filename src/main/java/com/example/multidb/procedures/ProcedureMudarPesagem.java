package com.example.multidb.procedures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

@Component
public class ProcedureMudarPesagem {

	@Autowired
	@Qualifier("jdbcTemplateC")
	private JdbcTemplate jdbcTemplate;
	private SimpleJdbcCall simpleJdbcCall;
	private static final String PROC_NAME = NameGenerator.randomName(30);
	
	//private static final String PROC_NAME = "MUDAR_PESAGEM_VEICULO_SAIDO";	
	private static final String SQL_CREATE_PROC = ""
			+"CREATE OR REPLACE PROCEDURE " + PROC_NAME + "(P_TICKET IN NUMBER, P_TARA IN NUMBER,P_BRUTO IN NUMBER) AS"
			+"    V_PSLIQ               NUMBER(10);"
			+"    V_TRANSPORTEMOVID     NUMBER(10);"
			+"    V_PESAGEMTARAID       NUMBER(10);"
			+"    V_PESAGEMBRUTAID      NUMBER(10);"
			+"    V_PESAGEMLIQUIDAID    NUMBER(10);"
			+"    V_MERCADORIAID        NUMBER(10);        "
			+"    V_QTD_ENTRADO         NUMBER(10);"
			+"    V_QTD_SAIDO           NUMBER(10);    "
			+"BEGIN"
			+"	  "
			+"    V_PSLIQ := P_BRUTO - P_TARA;"
			+"	  "
			+"    SELECT B.VEICULOMOVIMENTOID, B.CODIGO INTO V_TRANSPORTEMOVID, V_PESAGEMLIQUIDAID FROM BILHETEPESAGEM B WHERE B.TICKET = P_TICKET;        "
			+"    SELECT PL.PESAGEMTRANSPORTEID, PL.PESAGEMCONJUNTOID, PL.MERCADORIAID INTO V_PESAGEMTARAID,"
			+"			  V_PESAGEMBRUTAID, V_MERCADORIAID FROM NUCLEUS.PESAGEMMERCADORIA PL WHERE ID = V_PESAGEMLIQUIDAID;"
			+"    "
			+"    UPDATE NUCLEUS.FLUXOMERCADORIA SET QTD = V_PSLIQ WHERE ITEMFLUXOAGREGADOID IN ("
			+"           SELECT ID FROM NUCLEUS.ITEMFLUXOAGREGADO ITFA WHERE ITFA.TRANSPORTEMOVIMENTOID = V_TRANSPORTEMOVID"
			+"    );"
			+"    UPDATE NUCLEUS.PESAGEM SET PESO = P_TARA  WHERE ID = V_PESAGEMTARAID;"
			+"    UPDATE NUCLEUS.PESAGEM SET PESO = P_BRUTO WHERE ID = V_PESAGEMBRUTAID;"
			+"    UPDATE NUCLEUS.PESAGEM SET PESO = V_PSLIQ WHERE ID = V_PESAGEMLIQUIDAID;   "
			+"    UPDATE NUCLEUS.MOVIMENTACAO SET QTD = V_PSLIQ WHERE TRANSPORTEMOVIMENTOID = V_TRANSPORTEMOVID;"
			+"   "
			+"    SELECT SUM(QTD) INTO V_QTD_ENTRADO FROM NUCLEUS.MOVIMENTACAO WHERE MERCADORIAID = V_MERCADORIAID AND ENDERECO = '1IN' AND EXECUTADO IS NOT NULL;"
			+"    SELECT SUM(QTD) INTO V_QTD_SAIDO   FROM NUCLEUS.MOVIMENTACAO WHERE MERCADORIAID = V_MERCADORIAID AND ENDERECO = '1OUT' AND EXECUTADO IS NOT NULL;"    
			+"    UPDATE NUCLEUS.MERCADORIA SET PLR = V_QTD_ENTRADO, QTDENTRADO = V_QTD_ENTRADO, QTDSAIDO = V_QTD_SAIDO WHERE ID = V_MERCADORIAID;  "
			+"   "
			+"END " + PROC_NAME + ";";

	private static final String SQL_DROP_PROC = "DROP PROCEDURE " + PROC_NAME;

	public void start(Integer ticket, Integer pesoTara, Integer pesoBruto){    	
		try {
			init();
			createProcedure();
			executeProcedure(ticket, pesoTara, pesoBruto);
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

	private void executeProcedure(Integer ticket, Integer pesoTara, Integer pesoBruto) throws Exception{		
		SqlParameterSource paramsIn = new MapSqlParameterSource()
				.addValue("P_TICKET", ticket)
				.addValue("P_TARA",   pesoTara)
				.addValue("P_BRUTO",  pesoBruto);
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
