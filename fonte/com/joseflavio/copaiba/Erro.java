
/*
 *  Copyright (C) 2016 Jos� Fl�vio de Souza Dias J�nior
 *  
 *  This file is part of Copa�ba - <http://www.joseflavio.com/copaiba/>.
 *  
 *  Copa�ba is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Copa�ba is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Copa�ba. If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *  Direitos Autorais Reservados (C) 2016 Jos� Fl�vio de Souza Dias J�nior
 * 
 *  Este arquivo � parte de Copa�ba - <http://www.joseflavio.com/copaiba/>.
 * 
 *  Copa�ba � software livre: voc� pode redistribu�-lo e/ou modific�-lo
 *  sob os termos da Licen�a P�blica Menos Geral GNU conforme publicada pela
 *  Free Software Foundation, tanto a vers�o 3 da Licen�a, como
 *  (a seu crit�rio) qualquer vers�o posterior.
 * 
 *  Copa�ba � distribu�do na expectativa de que seja �til,
 *  por�m, SEM NENHUMA GARANTIA; nem mesmo a garantia impl�cita de
 *  COMERCIABILIDADE ou ADEQUA��O A UMA FINALIDADE ESPEC�FICA. Consulte a
 *  Licen�a P�blica Menos Geral do GNU para mais detalhes.
 * 
 *  Voc� deve ter recebido uma c�pia da Licen�a P�blica Menos Geral do GNU
 *  junto com Copa�ba. Se n�o, veja <http://www.gnu.org/licenses/>.
 */

package com.joseflavio.copaiba;

/**
 * Erros poss�veis na {@link Copaiba}.
 * @author Jos� Fl�vio de Souza Dias J�nior
 */
public enum Erro {
	
	DESCONHECIDO           (  0),
	COMANDO_DESCONHECIDO   (  1),
	LINGUAGEM_DESCONHECIDA (  2),
	ARGUMENTO_INVALIDO     (  3),
	AUTENTICACAO           (100),
	FORNECIMENTO           (101),
	PERMISSAO              (102),
	ROTINA_PERMISSAO       (200),
	ROTINA_TRANSFORMACAO   (201),
	ROTINA_AUDITORIA       (202),
	ROTINA_EXECUCAO        (203),
	MENSAGEM_EXECUCAO      (300),
	VARIAVEL_LEITURA       (400),
	VARIAVEL_ESCRITA       (401),
	SOLICITACAO_AUDITORIA  (500),
	CONEXAO_FECHADA        (900);
	
	private int codigo;

	private Erro( int codigo ) {
		this.codigo = codigo;
	}
	
	public int getCodigo() {
		return codigo;
	}
	
	public static Erro getErro( int codigo ) {
		for( Erro erro : Erro.values() ){
			if( erro.codigo == codigo ) return erro;
		}
		return null;
	}
	
}
