
package com.example.gastosimples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gastosimples.ui.theme.GastoSimplesTheme

sealed class Transacao(val descricao: String, val valor: Double) {
    class Receita(descricao: String, valor: Double) : Transacao(descricao, valor)
    class Despesa(descricao: String, valor: Double) : Transacao(descricao, valor)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GastoSimplesTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GestaoGastosApp()
                }
            }
        }
    }
}

@Composable
fun GestaoGastosApp() {
    var descricao by remember { mutableStateOf("") }
    var valor by remember { mutableStateOf("") }
    var tipoTransacao by remember { mutableStateOf("Receita") }
    var transacoes by remember { mutableStateOf(listOf<Transacao>()) }

    val totalReceitas = transacoes.filterIsInstance<Transacao.Receita>().sumOf { it.valor }
    val totalDespesas = transacoes.filterIsInstance<Transacao.Despesa>().sumOf { it.valor }
    val saldo = totalReceitas - totalDespesas

    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxSize()) {

        Text("Gestão de Gastos", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            RadioButton(
                selected = tipoTransacao == "Receita",
                onClick = { tipoTransacao = "Receita" }
            )
            Text("Receita", modifier = Modifier.padding(end = 16.dp))
            RadioButton(
                selected = tipoTransacao == "Despesa",
                onClick = { tipoTransacao = "Despesa" }
            )
            Text("Despesa")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val valorDouble = valor.toDoubleOrNull()
                if (descricao.isNotBlank() && valorDouble != null) {
                    val novaTransacao = if (tipoTransacao == "Receita") {
                        Transacao.Receita(descricao, valorDouble)
                    } else {
                        Transacao.Despesa(descricao, valorDouble)
                    }
                    transacoes = transacoes + novaTransacao
                    descricao = ""
                    valor = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Receitas: R$ %.2f".format(totalReceitas))
        Text("Despesas: R$ %.2f".format(totalDespesas))
        Text("Saldo atual: R$ %.2f".format(saldo), style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(transacoes.size) { index ->
                val t = transacoes[index]
                val prefix = if (t is Transacao.Receita) "+" else "-"
                Text("$prefix ${t.descricao}: R$ %.2f".format(t.valor))
            }
        }
    }
}
