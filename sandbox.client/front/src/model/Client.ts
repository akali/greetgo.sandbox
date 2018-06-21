import {GenderType} from "./GenderType";

export class Client {
  public id: number /* int */;
  public surname: string;
  public name: string;
  public patronymic: string;
  public gender: GenderType;
  public birth_date: Date;
  public charm: number /* int */;
}
