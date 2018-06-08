import {Component, OnInit} from "@angular/core";
import {FormGroup, FormBuilder} from "@angular/forms";
import {MatDialogRef} from "@angular/material";

@Component({
  templateUrl: './clientDialog.component.html'
})
export class ClientDialogComponent implements OnInit {
  // public form: FormGroup;
  //
  // constructor(private formBuilder: FormBuilder,
  //             private dialogRef: MatDialogRef<ClientDialogComponent>) {}

  ngOnInit(): void {
    // this.form = this.formBuilder.group({
    //   filename: ''
    // })
  }

  submit(form) {
    // this.dialogRef.close(`${form.value.filename}`);
  }
}
