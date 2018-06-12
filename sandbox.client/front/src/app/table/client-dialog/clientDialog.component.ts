import {Component, Inject, OnInit} from "@angular/core";
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators
} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDatepickerModule, MatDialogRef} from "@angular/material";
import {ClientDetail} from "../../../model/ClientDetail";
import {GenderType} from "../../../model/GenderType";
import {ClientPhone} from "../../../model/ClientPhone";
import {ClientAddress} from "../../../model/ClientAddress";
import {AddressType} from "../../../model/AddressType";
import {PhoneType} from "../../../model/PhoneType";
import {ActionType} from "./actionType";
import {Charm} from "../../../model/Charm";

@Component({
  templateUrl: './clientDialog.component.html',
  styleUrls: ['./clientDialog.component.css']
})
export class ClientDialogComponent implements OnInit {
  form: FormGroup;
  client: ClientDetail;
  genders = [GenderType.MALE, GenderType.FEMALE];
  action: ActionType;
  phoneTypes = [PhoneType.MOBILE, PhoneType.WORK, PhoneType.HOME];
  charmsCmp(c1: Charm, c2: Charm){
    return c1 && c2 ? c1.id === c2.id : c1 === c2;
  };

  constructor(
    private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<ClientDialogComponent>,
    @Inject(MAT_DIALOG_DATA) private data,
    private picker: MatDatepickerModule
  ) {}

  get phonesFormArray(): FormArray {
    return this.form.get('phones') as FormArray;
  }

  ngOnInit(): void {
    this.client = this.data.client;
    this.action = this.data.action;

    if (this.client.regAddress === undefined || this.client.regAddress === null) {
      this.client.regAddress = new ClientAddress(this.client.id, AddressType.REG, "", "", "");
    }
    if (this.client.factAddress === undefined || this.client.factAddress === null) {
      this.client.factAddress = new ClientAddress(this.client.id, AddressType.FACT, "", "", "");
    }

    console.log(this.client);
    this.form = this.formBuilder.group({
      id: [this.client.id],
      name: [this.client.name, Validators.required],
      surname: [this.client.surname, Validators.required],
      patronymic: this.client.patronymic,
      gender: [this.client.gender, Validators.required],
      birthDate: [new Date(this.client.birthDate), Validators.required],
      regAddress: this.formBuilder.group({
        street: [this.client.regAddress.street, Validators.required],
        house: [this.client.regAddress.house, Validators.required],
        flat: [this.client.regAddress.flat, Validators.required],
      }),
      factAddress: this.formBuilder.group({
        street: this.client.factAddress.street,
        house: this.client.factAddress.house,
        flat: this.client.factAddress.flat,
      }),
      charm: [this.client.charm, Validators.required],
      phones: this.formBuilder.array(this.client.phones.map(value => this.mobilePhoneGroup(value.number, value.type))),
    });
    this.form.controls['phones'].setValidators(atLeastOneMobileValidator());
    console.log(this.form.controls['charm']);
  }

  submit(form: FormGroup) {
    if (form.invalid) return;
    console.log('RawValue', this.form.getRawValue());
    let result = form.getRawValue();
    let phones = [];
    result.phones.forEach(value => phones.push(new ClientPhone(result.id, value.number, value.type)));
    result.phones = phones;
    result.birthDate = result.birthDate.getTime();
    result.charm = result.charm.id;
    this.dialogRef.close(result);
  }

  cancel(form) {
    form.reset();
    this.dialogRef.close();
  }

  public addNumber() {
    this.phonesFormArray.push(this.mobilePhoneGroup(''));
  }

  private mobilePhoneGroup(value: string, type: PhoneType = PhoneType.MOBILE) {
    return this.formBuilder.group({
      type: this.formBuilder.control(type),
      number: this.formBuilder.control(value, [
        Validators.required
      ])
    })
  }

  isCreate() {
    return this.action === ActionType.CREATE;
  }

  deleteNumber(idx) {
    this.phonesFormArray.removeAt(idx);
  }
}

export function atLeastOneMobileValidator(): ValidatorFn {
  return (array: AbstractControl): ValidationErrors | null => {
    if (!array || !array.value) {
      return null;
    }
    let phones = array.value;
    for (let key in phones) {
      if (phones.hasOwnProperty(key)) {
        let type = phones[key].type;
        console.log(type === PhoneType.MOBILE);
        if (type === PhoneType.MOBILE) return null;
      }
    }
    return {atLeastOneMobile: true};
  };
}
