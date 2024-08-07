import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatRadioModule } from '@angular/material/radio';
import { MatInputModule } from '@angular/material/input';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatMenuModule } from '@angular/material/menu';
import { MatCardModule } from '@angular/material/card';

const MATERIAL = [
  MatFormFieldModule,
  MatRadioModule,
  MatInputModule,
  MatDatepickerModule,
  MatButtonModule,
  MatCheckboxModule,
  MatIconModule,
  MatButtonToggleModule,
  MatMenuModule,
  MatCardModule
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    MATERIAL
  ],
  exports: [
    MATERIAL
  ],
  providers: [
    provideNativeDateAdapter()
  ]
})

export class NgMaterialsModule { }