package com.example.rangkul.ui.profile

import androidx.lifecycle.ViewModel
import com.example.rangkul.data.model.ReportData
import com.example.rangkul.data.model.UserData
import com.example.rangkul.data.repository.OptionsRepository
import com.example.rangkul.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VisitedProfileOptionsViewModel @Inject constructor(private val repository: OptionsRepository): ViewModel() {

    fun addReportData(report: ReportData, result: (UiState<Boolean>) -> Unit){
        repository.addReportData(report) {
            result.invoke(it)
        }
    }

    fun getSessionData(result: (UserData?) -> Unit) {
        repository.getSessionData(result)
    }

}