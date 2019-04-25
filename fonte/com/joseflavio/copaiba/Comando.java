
/*
 *  Copyright (C) 2016-2018 José Flávio de Souza Dias Júnior
 *  
 *  This file is part of Copaíba - <http://joseflavio.com/copaiba/>.
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
 *  Direitos Autorais Reservados (C) 2016-2018 José Flávio de Souza Dias Júnior
 * 
 *  Este arquivo é parte de Copaíba - <http://joseflavio.com/copaiba/>.
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

import java.io.File;

import com.joseflavio.urucum.comunicacao.Notificacao;

/**
 * Comandos do protocolo de comunicação da {@link Copaiba}.
 * @author José Flávio de Souza Dias Júnior
 */
public enum Comando {
	
	/**
	 * Inicia uma conexão no {@link Modo#JAVA}.
	 */
	INICIO_JAVA (1),
	
	/**
	 * Inicia uma conexão no {@link Modo#JSON}.
	 */
	INICIO_JSON (2),
	
	/**
	 * Conexão temporária para se obter {@link Informacao}.
	 */
	INICIO_INFORMACAO (3),
	
	/**
	 * Conexão temporária para escrita (upload) de {@link File arquivo}.
	 * @see Servico#registrarTransferencia(File, Notificacao, Notificacao)
	 */
	INICIO_ARQUIVO_ESCRITA (4),
	
	/**
	 * Conexão temporária para leitura (download) de {@link File arquivo}.
	 * @see Servico#registrarTransferencia(File, Notificacao, Notificacao)
	 */
	INICIO_ARQUIVO_LEITURA (5),
	
	FIM (20),
	SUCESSO (21),
	ERRO (22),
	VERIFICACAO (23),
	ROTINA (40),
	MENSAGEM (41),
	VARIAVEL_ESCRITA (42),
	VARIAVEL_LEITURA (43),
	VARIAVEL_REMOCAO (44),
	SOLICITACAO (45);
	
	private byte codigo;

	private Comando( int codigo ) {
		this.codigo = (byte) codigo;
	}
	
	public byte getCodigo() {
		return codigo;
	}
	
	public static Comando getComando( byte codigo ) {
		for( Comando comando : Comando.values() ){
			if( comando.codigo == codigo ) return comando;
		}
		return null;
	}
	
}
