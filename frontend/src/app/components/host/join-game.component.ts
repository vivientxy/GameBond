import { Component, OnInit, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { GameService } from '../../services/game.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-join-game',
  templateUrl: './join-game.component.html',
  styleUrl: './join-game.component.css'
})
export class JoinGameComponent implements OnInit {

  private readonly fb = inject(FormBuilder)
  private readonly gameSvc = inject(GameService)
  private readonly router = inject(Router)
  form!: FormGroup

  ngOnInit(): void {
    this.form = this.fb.group({
      hostId: this.fb.control('', [Validators.required, Validators.minLength(8), Validators.maxLength(8)])
    })
  }

  processJoinGame() {
    let hostId: string = this.form.controls['hostId'].value
    hostId = hostId.toLowerCase()
    console.log('>>> host ID:', hostId)

    // generate URL and redirect
    const telegramLink = this.gameSvc.generateTelegramLink(hostId);
    console.log('>>> telegram link:', telegramLink)

    window.location.href = telegramLink;


  }

}
