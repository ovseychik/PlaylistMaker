package com.example.playlistmaker.di

import com.example.playlistmaker.main.ui.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel {
        MainViewModel(get()) // TODO get something
    }
}