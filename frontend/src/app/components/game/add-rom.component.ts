import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GameService } from '../../services/game.service';
import { User } from '../../models/user.model';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-add-rom',
  templateUrl: './add-rom.component.html',
  styleUrl: './add-rom.component.css'
})
export class AddRomComponent implements OnInit {

  private readonly fb = inject(FormBuilder)
  private readonly router = inject(Router)
  private readonly gameSvc = inject(GameService)
  private readonly userSvc = inject(UserService)
  romForm!: FormGroup;
  selectedRomFile: any = null;
  selectedPicFile: any = null;
  user!: User | null;

  ngOnInit(): void {
    this.user = this.userSvc.getUser()
    this.romForm = this.fb.group({
      romFile: this.fb.control('', [Validators.required]),
      picFile: this.fb.control('')
    })
  }

  addRom() {
    const formData = new FormData();
    const romFileValue = this.romForm.get('romFile')?.value
    const picFileValue = this.romForm.get('picFile')?.value

    if (romFileValue !== null && romFileValue !== undefined)
      formData.append('rom', romFileValue)
    if (picFileValue !== null && picFileValue !== undefined)
      formData.append('pic', picFileValue)
    if (this.user)
      formData.append('username', this.user.username)

    this.gameSvc.addGameROM(formData)
      .then(resp => {
        alert('ROM uploaded successfully!')
        this.router.navigate(['/host'])
      })
      .catch(err => {alert(err.error)})
  }

  onFileSelectedRom(event: any): void {
    this.selectedRomFile = event.target.files[0] ?? null;
    this.romForm.patchValue({
      romFile: this.selectedRomFile
    })
  }

  onFileSelectedPic(event: any): void {
    this.selectedPicFile = event.target.files[0] ?? null;
    this.romForm.patchValue({
      picFile: this.selectedPicFile
    })
  }

}
