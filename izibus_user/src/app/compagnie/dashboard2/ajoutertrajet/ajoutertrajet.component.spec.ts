import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AjoutertrajetComponent } from './ajoutertrajet.component';

describe('AjoutertrajetComponent', () => {
  let component: AjoutertrajetComponent;
  let fixture: ComponentFixture<AjoutertrajetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AjoutertrajetComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AjoutertrajetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
