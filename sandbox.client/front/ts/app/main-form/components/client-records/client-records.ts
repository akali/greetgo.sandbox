import {Component, OnDestroy} from "@angular/core";
import {HttpService} from "../../../HttpService";
import {MatDialog, MatDialogConfig} from "@angular/material";
import {ModalClientDetailsComponent} from "./components/modal-client-details/modal-client-details";
import {ActionType} from "../../../../utils/ActionType";

@Component({
  selector: 'client-records-component',
  template: require('./client-records.html'),
  styles: [require('./client-records.css')]
})
export class ClientRecordsComponent {

  constructor(private httpService: HttpService, private dialog: MatDialog) { }

  handleCreateAccClick = function () {
    this.openModal(null, ActionType.CREATE);
  };

  handleEditAccClick = function (accountInfo) {
    this.openModal(accountInfo.clientId, ActionType.EDIT);
  };

  openModal(clientId: number, actionType: ActionType) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.data = {clientId: clientId, actionType: actionType};

    const dialogRef = this.dialog.open(ModalClientDetailsComponent, dialogConfig);

    dialogRef.afterClosed().subscribe(data => {
      console.log(data);
    })
  }
}
