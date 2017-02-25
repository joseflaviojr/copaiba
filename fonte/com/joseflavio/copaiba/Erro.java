
/*
 *  Copyright (C) 2016 José Flávio de Souza Dias Júnior
 *  
 *  This file is part of Copaíba - <http://www.joseflavio.com/copaiba/>.
 *  
 *  Copaíba is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  Copaíba is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Copaíba. If not, see <http://www.gnu.org/licenses/>.
 */

/*
 *  Direitos Autorais Reservados (C) 2016 José Flávio de Souza Dias Júnior
 * 
 *  Este arquivo é parte de Copaíba - <http://www.joseflavio.com/copaiba/>.
 * 
 *  Copaíba é software livre: você pode redistribuí-lo e/ou modificá-lo
 *  sob os termos da Licença Pública Menos Geral GNU conforme publicada pela
 *  Free Software Foundation, tanto a versão 3 da Licença, como
 *  (a seu critério) qualquer versão posterior.
 * 
 *  Copaíba é distribuído na expectativa de que seja útil,
 *  porém, SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 *  COMERCIABILIDADE ou ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA. Consulte a
 *  Licença Pública Menos Geral do GNU para mais detalhes.
 * 
 *  Você deve ter recebido uma cópia da Licença Pública Menos Geral do GNU
 *  junto com Copaíba. Se não, veja <http://www.gnu.org/licenses/>.
 */

package com.joseflavio.copaiba;

/**
 * Erros possíveis na {@link Copaiba}.
 * @author José Flávio de Souza Dias Júnior
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
