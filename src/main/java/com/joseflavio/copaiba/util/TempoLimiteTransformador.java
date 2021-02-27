
/*
 *  Copyright (C) 2016-2021 José Flávio de Souza Dias Júnior
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
 *  Direitos Autorais Reservados (C) 2016-2021 José Flávio de Souza Dias Júnior
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

package com.joseflavio.copaiba.util;

import com.joseflavio.copaiba.CopaibaConexao;
import com.joseflavio.copaiba.CopaibaException;
import com.joseflavio.copaiba.Erro;
import com.joseflavio.copaiba.Transformador;
import com.joseflavio.copaiba.Usuario;

import groovy.transform.TimedInterrupt;

/**
 * {@link Transformador} que tenta limitar o tempo de execução de uma {@link CopaibaConexao#executar(String, String, java.io.Writer) rotina},
 * através de técnica específica e otimizada da linguagem em questão.<br>
 * A limitação de tempo não é garantida, principalmente quando se utiliza instruções bloqueantes, como as de entrada/saída.<br>
 * Algumas linguagens não possuem limitadores temporais.<br>
 * Rotinas em Groovy serão ajustadas com {@link TimedInterrupt}.
 * @author José Flávio de Souza Dias Júnior
 */
public class TempoLimiteTransformador implements Transformador {
	
	private int tempoLimite;
	
	/**
	 * @param tempoLimite Tempo limite de execução desejado, em segundos.
	 */
	public TempoLimiteTransformador( int tempoLimite ) {
		this.tempoLimite = tempoLimite;
	}

	@Override
	public String transformar( Usuario usuario, String linguagem, String rotina ) throws CopaibaException {
		
		if( linguagem == null || rotina == null ){
			throw new CopaibaException( Erro.ROTINA_TRANSFORMACAO, "null" );
		}
		
		linguagem = linguagem.toLowerCase();
		
		if( linguagem.equals( "groovy" ) ){
			rotina =
				"import groovy.transform.TimedInterrupt\n" +
				"import java.util.concurrent.TimeUnit\n" +
				"@TimedInterrupt(value=" + tempoLimite + ", unit=TimeUnit.SECONDS)\n" +
				"import java.lang.*\n" +
				rotina;
		}
		
		return rotina;
		
	}

}
