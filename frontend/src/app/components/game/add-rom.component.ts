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
  selectedFile: any = null;
  user!: User | null;

  ngOnInit(): void {
    this.user = this.userSvc.getUser()
    if (!this.user)
      this.router.navigate(['/']);

    this.romForm = this.fb.group({
      fileSource: this.fb.control('', [Validators.required])
    })
  }

  addRom() {
    const formData = new FormData();
    const fileSourceValue = this.romForm.get('fileSource')?.value

    if (fileSourceValue !== null && fileSourceValue !== undefined)
      formData.append('rom', fileSourceValue)

    console.log('>>> this.user:', this.user)
    if (this.user)
      formData.append('username', this.user.username)

    this.gameSvc.addGameROM(formData)
      .subscribe(response => {
        console.log('>>> http response:', response)
        alert('ROM uploaded successfully!')
        this.router.navigate(['/host'])
      })
  }

  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0] ?? null;
    if (event.target.files.length > 0) {
      this.romForm.patchValue({
        fileSource: this.selectedFile
      })
    }
  }

}
