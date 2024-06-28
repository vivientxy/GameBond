import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GameService } from '../../services/game.service';

@Component({
  selector: 'app-add-rom',
  templateUrl: './add-rom.component.html',
  styleUrl: './add-rom.component.css'
})
export class AddRomComponent implements OnInit {

  private readonly fb = inject(FormBuilder)
  private readonly gameSvc = inject(GameService)
  romForm!: FormGroup;
  selectedFile: any = null;
  username!: string;

  ngOnInit(): void {
    this.romForm = this.fb.group({
      fileSource: this.fb.control('', [Validators.required])
    })

    // grab username from user.store.ts --> to create
  }

  addRom() {
    const formData = new FormData();
    const fileSourceValue = this.romForm.get('fileSource')?.value

    if (fileSourceValue !== null && fileSourceValue !== undefined)
      formData.append('rom', fileSourceValue)

    formData.append('username', this.username)

    this.gameSvc.addGameROM(formData)
      .subscribe(response => {
        console.log('>>> http response:', response)
        alert('File uploaded successfully!')
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
