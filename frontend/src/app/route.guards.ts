import { inject } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from "@angular/router";
import { UserService } from "./services/user.service";
import { GameService } from "./services/game.service";


export const loginGuard: CanActivateFn =
    (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
        const userSvc = inject(UserService)
        const router = inject(Router)

        if (!userSvc.isLoggedIn()) {
            router.navigate(['/login']);
            return false;
        }
        return true;
    }

export const gameGuard: CanActivateFn =
    (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
        const gameSvc = inject(GameService)
        const router = inject(Router)

        if (!gameSvc.getGame()) {
          router.navigate(['/'])
          return false;
        }
        return true;
    }