import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListetrajetComponent } from './listetrajet.component';

describe('ListetrajetComponent', () => {
  let component: ListetrajetComponent;
  let fixture: ComponentFixture<ListetrajetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ListetrajetComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListetrajetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
