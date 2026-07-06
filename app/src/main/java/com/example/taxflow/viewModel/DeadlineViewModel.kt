package com.example.taxflow.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taxflow.data.repository.TaxDeadlineRepository
import com.example.taxflow.domain.model.TaxDeadline
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class DeadlinesViewModel(
    private val repository: TaxDeadlineRepository
) : ViewModel() {

    val deadlines: StateFlow<List<TaxDeadline>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addDeadline(title: String, dueDate: LocalDate, note: String = "") {
        viewModelScope.launch {
            repository.add(TaxDeadline(title = title, dueDate = dueDate, note = note))
        }
    }

    fun togglePaid(deadline: TaxDeadline) {
        viewModelScope.launch {
            repository.update(deadline.copy(isPaid = !deadline.isPaid))
        }
    }

    fun delete(deadline: TaxDeadline) {
        viewModelScope.launch { repository.delete(deadline) }
    }
}