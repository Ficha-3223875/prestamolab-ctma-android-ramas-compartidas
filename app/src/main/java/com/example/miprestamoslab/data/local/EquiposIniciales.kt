package com.example.miprestamoslab.data.local

import com.example.miprestamoslab.model.CategoriaEquipo
import com.example.miprestamoslab.model.Equipo
import com.example.miprestamoslab.model.EstadoEquipo

object EquiposIniciales {
    val lista: List<Equipo> = listOf(
        Equipo(1, "Multímetro Digital", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE,
            "Multímetro para mediciones de voltaje, corriente y resistencia."),
        Equipo(2, "Kit Arduino Uno", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE,
            "Kit de desarrollo con placa Arduino Uno, cables y sensores."),
        Equipo(3, "Tablet Samsung", CategoriaEquipo.TABLETA, EstadoEquipo.DISPONIBLE,
            "Tablet Samsung de 10 pulgadas para actividades de formación."),
        Equipo(4, "Cámara DSLR Canon", CategoriaEquipo.CAMARA, EstadoEquipo.DISPONIBLE,
            "Cámara réflex digital para registro fotográfico de prácticas."),
        Equipo(5, "Soldador de Estaño", CategoriaEquipo.HERRAMIENTA, EstadoEquipo.DISPONIBLE,
            "Cautín de estaño de 60W con soporte y esponja."),
        Equipo(6, "Teclado Mecánico", CategoriaEquipo.PERIFERICO, EstadoEquipo.DISPONIBLE,
            "Teclado mecánico USB con retroiluminación."),
        Equipo(7, "Osciloscopio USB", CategoriaEquipo.ELECTRONICA, EstadoEquipo.DISPONIBLE,
            "Osciloscopio digital de bolsillo compatible con PC."),
        Equipo(8, "Set Destornilladores", CategoriaEquipo.HERRAMIENTA, EstadoEquipo.DISPONIBLE,
            "Set de destornilladores de precisión con puntas intercambiables.")
    )
}